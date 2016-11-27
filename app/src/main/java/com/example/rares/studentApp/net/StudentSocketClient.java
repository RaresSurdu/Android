package com.studentApp.net;

/**
 * Created by Rares on 27.11.2016.
 */
import android.content.Context;
import android.util.Log;

import com.studentApp.R;
import com.studentApp.content.Student;
import com.studentApp.net.mapping.IdJsonObjectReader;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.studentApp.net.mapping.Api.Student.STUDENT_CREATED;
import static com.studentApp.net.mapping.Api.Student.STUDENT_DELETED;
import static com.studentApp.net.mapping.Api.Student.STUDENT_UPDATED;


public class StudentSocketClient {
    private static final String TAG = StudentSocketClient.class.getSimpleName();
    private final Context mContext;
    private Socket mSocket;
    private ResourceChangeListener<Student> mResourceListener;

    public StudentSocketClient(Context context) {
        mContext = context;
        Log.d(TAG, "created");
    }

    public void subscribe(final ResourceChangeListener<Student> resourceListener) {
        Log.d(TAG, "subscribe");
        mResourceListener = resourceListener;
        try {
            mSocket = IO.socket(mContext.getString(R.string.api_url));
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "socket connected");
                }
            });
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "socket disconnected");
                }
            });
            mSocket.on(STUDENT_CREATED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Student student = new com.studentApp.net.mapping.StudentJsonObjectReader().read((JSONObject) args[0]);
                        Log.d(TAG, String.format("student created %s", student.toString()));
                        mResourceListener.onCreated(student);
                    } catch (Exception e) {
                        Log.w(TAG, "student created", e);
                        mResourceListener.onError(new ResourceException(e));
                    }
                }
            });
            mSocket.on(STUDENT_UPDATED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Student student = new com.studentApp.net.mapping.StudentJsonObjectReader().read((JSONObject) args[0]);
                        Log.d(TAG, String.format("student updated %s", student.toString()));
                        mResourceListener.onUpdated(student);
                    } catch (Exception e) {
                        Log.w(TAG, "student updated", e);
                        mResourceListener.onError(new ResourceException(e));
                    }
                }
            });
            mSocket.on(STUDENT_DELETED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        String id = new IdJsonObjectReader().read((JSONObject) args[0]);
                        Log.d(TAG, String.format("student deleted %s", id));
                        mResourceListener.onDeleted(id);
                    } catch (Exception e) {
                        Log.w(TAG, "student deleted", e);
                        mResourceListener.onError(new ResourceException(e));
                    }
                }
            });
            mSocket.connect();
        } catch (Exception e) {
            Log.w(TAG, "socket error", e);
            mResourceListener.onError(new ResourceException(e));
        }
    }

    public void unsubscribe() {
        Log.d(TAG, "unsubscribe");
        if (mSocket != null) {
            mSocket.disconnect();
        }
        mResourceListener = null;
    }

}
