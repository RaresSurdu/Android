package com.studentApp.service;

/**
 * Created by Rares on 27.11.2016.
 */
import android.content.Context;
import android.util.Log;

import com.studentApp.content.Student;
import com.studentApp.net.LastModifiedList;
import com.studentApp.util.CancellableCallable;
import com.studentApp.util.OkAsyncTaskLoader;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class StudentLoader extends OkAsyncTaskLoader<List<Student> implements Observer {
    private static final String TAG = StudentLoader.class.getSimpleName();
    private final StudentManager mStudentManager;
    private List<Student> mCachedStudents;
    private CancellableCallable<LastModifiedList<Student>> mCancellableCall;

    public StudentLoader(Context context, StudentManager studentManager) {
        super(context);
        mStudentManager = studentManager;
    }

    @Override
    public List<Student> tryLoadInBackground() throws Exception {
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client
        Log.d(TAG, "tryLoadInBackground");
        mCancellableCall = mStudentManager.getStudentsCall();
        mCachedStudents = mStudentManager.executeStudentsCall(mCancellableCall);
        return mCachedStudents;
    }

    @Override
    public void deliverResult(List<Student> data) {
        Log.d(TAG, "deliverResult");
        if (isReset()) {
            Log.d(TAG, "deliverResult isReset");
            // The Loader has been reset; ignore the result and invalidate the data.
            return;
        }
        mCachedStudents = data;
        if (isStarted()) {
            Log.d(TAG, "deliverResult isStarted");
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        if (mCachedStudents != null) {
            Log.d(TAG, "onStartLoading cached not null");
            // Deliver any previously loaded data immediately.
            deliverResult(mCachedStudents);
        }
        // Begin monitoring the underlying data source.
        mStudentManager.addObserver(this);
        if (takeContentChanged() || mCachedStudents == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            Log.d(TAG, "onStartLoading cached null force reload");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        Log.d(TAG, "onStopLoading");
        cancelLoad();
        // Bill that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        Log.d(TAG, "onReset");
        onStopLoading();
        // At this point we can release the resources associated with 'mData'.
        if (mCachedStudents != null) {
            mCachedStudents = null;
        }
        // The Loader is being reset, so we should stop monitoring for changes.
        mStudentManager.deleteObserver(this);
    }

    @Override
    public void onCanceled(List<Student> data) {
        // Attempt to cancel the current asynchronous load.
        Log.d(TAG, "onCanceled");
        super.onCanceled(data);
    }

    @Override
    public void update(Observable o, Object arg) {
        mCachedStudents = mStudentManager.getCachedStudents();
    }
}
