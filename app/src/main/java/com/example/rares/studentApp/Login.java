package com.studentApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.studentApp.content.User;
import com.studentApp.service.StudentManager;
import com.studentApp.util.Cancellable;
import com.studentApp.util.DialogUtils;
import com.studentApp.util.OnErrorListener;
import com.studentApp.util.OnSuccessListener;

public class Login extends AppCompatActivity {

  private Cancellable mCancellable;
  private StudentManager mStudentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    mStudentManager = ((App) getApplication()).getStudentManager();
    User user = mStudentManager.getCurrentUser();
    if (user != null) {
      startBillListActivity();
      finish();
    }
    setupToolbar();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mCancellable != null) {
      mCancellable.cancel();
    }
  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        login();
        Snackbar.make(view, "Authenticating, please wait", Snackbar.LENGTH_INDEFINITE)
            .setAction("Action", null).show();
      }
    });
  }

  private void login() {
    EditText usernameEditText = (EditText) findViewById(R.id.username);
    EditText passwordEditText = (EditText) findViewById(R.id.password);
    mCancellable = mStudentManager
        .loginAsync(
            usernameEditText.getText().toString(), passwordEditText.getText().toString(),
            new OnSuccessListener<String>() {
              @Override
              public void onSuccess(String s) {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    startStudentListActivity();
                  }
                });
              }
            }, new OnErrorListener() {
              @Override
              public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    DialogUtils.showError(Login.this, e);
                  }
                });
              }
            });
  }

  private void startStudentListActivity() {
    startActivity(new Intent(this, StudentListActivity.class));
  }
}
