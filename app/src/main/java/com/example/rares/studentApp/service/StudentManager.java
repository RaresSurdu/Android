package com.studentApp.service;

/**
 * Created by Rares on 27.11.2016.
 */
import android.content.Context;
import android.util.Log;

import com.studentApp.content.Student;
import com.studentApp.content.User;
import com.studentApp.content.database.Database;
import com.studentApp.net.*;
import com.studentApp.util.Cancellable;
import com.studentApp.util.CancellableCallable;
import com.studentApp.util.OnErrorListener;
import com.studentApp.util.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StudentManager extends Observable {
    private static final String TAG = StudentManager.class.getSimpleName();
    private final Database mKD;

    private ConcurrentMap<String, Student> mStudents = new ConcurrentHashMap<String, Student>();
    private String mStudentsLastUpdate;

    private final Context mContext;
    private StudentRestClient mStudentRestClient;
    private StudentSocketClient mStudentSocketClient;
    private String mToken;
    private User mCurrentUser;

    public StudentManager(Context context) {
        mContext = context;
        mKD = new Database(context);
    }

    public CancellableCallable<LastModifiedList<Student>> getBillsCall() {
        Log.d(TAG, "getStudentsCall");
        return mStudentRestClient.search(mStudentsLastUpdate);
    }

    public List<Student> executeStudentsCall(CancellableCallable<LastModifiedList<Student>> getBillsCall) throws Exception {
        Log.d(TAG, "execute getStudents...");
        LastModifiedList<Student> result = getStudentsCall.call();
        List<Student> students = result.getList();
        if (students != null) {
            mStudentsLastUpdate = result.getLastModified();
            updateCachedBills(students);
            notifyObservers();
        }
        return cachedBillsByUpdated();
    }

    public StudentLoader getStudentLoader() {
        Log.d(TAG, "getStudentLoader...");
        return new StudentLoader(mContext, this);
    }

    public void setStudentRestClient(StudentRestClient studentRestClient) {
        mStudentRestClient = studentRestClient;
    }

    public Cancellable getStudentsAsync(final OnSuccessListener<List<Student>> successListener, OnErrorListener errorListener) {
        Log.d(TAG, "getStudentsAsync...");
        return mStudentRestClient.searchAsync(mStudentsLastUpdate, new OnSuccessListener<LastModifiedList<Student>>() {

            @Override
            public void onSuccess(LastModifiedList<Student> result) {
                Log.d(TAG, "getStudentsAsync succeeded");
                List<Student> students = result.getList();
                if (students != null) {
                    mStudentsLastUpdate = result.getLastModified();
                    updateCachedBills(students);
                }
                successListener.onSuccess(cachedStudentsByUpdated());
                notifyObservers();
            }
        }, errorListener);
    }

    public Cancellable getBillAsync(
            final String billId,
            final OnSuccessListener<Student> successListener,
            final OnErrorListener errorListener) {
        Log.d(TAG, "getStudentAsync...");
        return mStudentRestClient.readAsync(billId, new OnSuccessListener<Student>() {

            @Override
            public void onSuccess(Student student) {
                Log.d(TAG, "getBillAsync succeeded");
                if (student == null) {
                    setChanged();
                    mStudents.remove(billId);
                } else {
                    if (!student.equals(mStudents.get(student.getId()))) {
                        setChanged();
                        mStudents.put(billId, student);
                    }
                }
                successListener.onSuccess(student);
                notifyObservers();
            }
        }, errorListener);
    }

    public Cancellable saveStudentAsync(
            final Student student,
            final OnSuccessListener<Student> successListener,
            final OnErrorListener errorListener) {
        Log.d(TAG, "saveStudentAsync...");
        return mStudentRestClient.updateAsync(student, new OnSuccessListener<Student>() {

            @Override
            public void onSuccess(Student student) {
                Log.d(TAG, "saveBillAsync succeeded");
                mStudents.put(student.getId(), student);
                successListener.onSuccess(student);
                setChanged();
                notifyObservers();
            }
        }, errorListener);
    }

    public void subscribeChangeListener() {
        mStudentSocketClient.subscribe(new ResourceChangeListener<Student>() {
            @Override
            public void onCreated(Student student) {
                Log.d(TAG, "changeListener, onCreated");
                ensureBillCached(student);
            }

            @Override
            public void onUpdated(Student student) {
                Log.d(TAG, "changeListener, onUpdated");
                ensureBillCached(student);
            }

            @Override
            public void onDeleted(String billId) {
                Log.d(TAG, "changeListener, onDeleted");
                if (mStudents.remove(billId) != null) {
                    setChanged();
                    notifyObservers();
                }
            }

            private void ensureStudentCached(Student student) {
                if (!student.equals(mStudents.get(student.getId()))) {
                    Log.d(TAG, "changeListener, cache updated");
                    mStudents.put(student.getId(), student);
                    setChanged();
                    notifyObservers();
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "changeListener, error", t);
            }
        });
    }

    public void unsubscribeChangeListener() {
        mStudentSocketClient.unsubscribe();
    }

    public void setBillSocketClient(StudentSocketClient studentSocketClient) {
        mStudentSocketClient = studentSocketClient;
    }

    private void updateCachedStudents(List<Student> students) {
        Log.d(TAG, "updateCachedBills");
        for (Student student : students) {
            mBilStudents.put(student.getId(), student);
        }
        setChanged();
    }

    private List<Student> cachedStudentsByUpdated() {
        ArrayList<Student> students = new ArrayList<>(mStudents.values());
        Collections.sort(students, new BillByUpdatedComparator());
        return students;
    }

    public List<Student> getCachedStudents() {
        return cachedStudentsByUpdated();
    }

    public Cancellable loginAsync(
            String username, String password,
            final OnSuccessListener<String> successListener,
            final OnErrorListener errorListener) {
        final User user = new User(username, password);
        return mStudentRestClient.getToken(
                user, new OnSuccessListener<String>() {

                    @Override
                    public void onSuccess(String token) {
                        mToken = token;
                        if (mToken != null) {
                            user.setToken(mToken);
                            setCurrentUser(user);
                            mKD.saveUser(user);
                            successListener.onSuccess(mToken);
                        } else {
                            errorListener.onError(new ResourceException(new IllegalArgumentException("Invalid credentials")));
                        }
                    }
                }, errorListener);
    }

    public void setCurrentUser(User currentUser) {
        mCurrentUser = currentUser;
        mStudentRestClient.setUser(currentUser);
    }

    public User getCurrentUser() {
        return mKD.getCurrentUser();
    }

    private class BillByUpdatedComparator implements java.util.Comparator<Student> {
        @Override
        public int compare(Student s1, Student s2) {
            return (int) (s1.getUpdated() - s2.getUpdated());
        }
    }
}

