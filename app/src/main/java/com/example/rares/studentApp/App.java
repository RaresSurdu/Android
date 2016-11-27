package com.example.rares.studentApp.;

import android.app.Application;
import android.util.Log;

import com.studentApp.net.StudentRestClient;
import com.studentApp.net.StudentSocketClient;
import com.studentApp.service.StudentManager;

public class App extends Application {
  public static final String TAG = App.class.getSimpleName();
  private StudentManager mStudentManager;
  private StudentRestClient mStudentRestClient;
  private StudentSocketClient mStudentSocketClient;

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate");
    super.onCreate();
    //ttt
    mStudentManager = new StudentManager(this);
    mStudentRestClient = new StudentRestClient(this);
    mStudentSocketClient = new StudentSocketClient(this);
    mStudentManager.setStudentRestClient(mStudentRestClient);
    mStudentManager.setStudentSocketClient(mStudentSocketClient);
  }

  public StudentManager getStudentManager() {
    return mStudentManager;
  }

  @Override
  public void onTerminate() {
    Log.d(TAG, "onTerminate");
    super.onTerminate();
  }
}
