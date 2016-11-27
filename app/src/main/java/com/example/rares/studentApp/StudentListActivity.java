package com.studentApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studentApp.content.Student;
import com.studentApp.util.Cancellable;
import com.studentApp.util.DialogUtils;
import com.studentApp.util.OnErrorListener;
import com.studentApp.util.OnSuccessListener;
import com.studentApp.content.Student;

import java.util.List;

/**
 * An activity representing a list of Students. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StudentDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StudentListActivity extends AppCompatActivity {

    public static final String TAG = StudentListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    /**
     * Whether or not the the students were loaded.
     */
    private boolean mStudentLoaded;

    /**
     * Reference to the singleton app used to access the app state and logic.
     */
    private App mApp;

    /**
     * Reference to the last async call used for cancellation.
     */
    private Cancellable mGetStudentsAsyncCall;
    private View mContentLoadingView;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mApp = (App) getApplication();
        setContentView(R.layout.activity_bill_list);
        setupToolbar();
        setupFloatingActionBar();
        setupRecyclerView();
        checkTwoPaneMode();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        startGetStudentsAsyncCall();
        mApp.getStudentManager().subscribeChangeListener();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        //ensureGetStudentsAsyncTaskCancelled();
        ensureGetStudentsAsyncCallCancelled();
        mApp.getStudentManager().unsubscribeChangeListener();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    private void setupFloatingActionBar() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new Student();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setupRecyclerView() {
        mContentLoadingView = findViewById(R.id.content_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.bill_list);
    }

    private void checkTwoPaneMode() {
        if (findViewById(R.id.bill_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void startGetStudentsAsyncCall() {
        if (mBillsLoaded) {
            Log.d(TAG, "start getStudentsAsyncCall - content already loaded, return");
            return;
        }
        showLoadingIndicator();
        mGetStudentsAsyncCall = mApp.getStudentManager().getStudentsAsync(
                new OnSuccessListener<List<Student>>() {
                    @Override
                    public void onSuccess(final List<Student> students) {
                        Log.d(TAG, "getStudentsAsyncCall - success");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showContent(students);
                            }
                        });
                    }
                }, new OnErrorListener() {
                    @Override
                    public void onError(final Exception e) {
                        Log.d(TAG, "getStudentsAsyncCall - error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showError(e);
                            }
                        });
                    }
                }
        );
    }


    private void ensureGetStudentsAsyncCallCancelled() {
        if (mGetStudentsAsyncCall != null) {
            Log.d(TAG, "ensureGetStudentsAsyncCallCancelled - cancelling the task");
            mGetStudentsAsyncCall.cancel();
        }
    }

    private void showError(Exception e) {
        Log.e(TAG, "showError", e);
        if (mContentLoadingView.getVisibility() == View.VISIBLE) {
            mContentLoadingView.setVisibility(View.GONE);
        }
        DialogUtils.showError(this, e);
    }

    private void showLoadingIndicator() {
        Log.d(TAG, "showLoadingIndicator");
        mRecyclerView.setVisibility(View.GONE);
        mContentLoadingView.setVisibility(View.VISIBLE);
    }

    private void showContent(final List<Student> students) {
        Log.d(TAG, "showContent");
        mRecyclerView.setAdapter(new StudentRecyclerViewAdapter(students));
        mContentLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public class StudentRecyclerViewAdapter
            extends RecyclerView.Adapter<StudentRecyclerViewAdapter.ViewHolder> {

        private final List<Student> mValues;

        public StudentRecyclerViewAdapter(List<Student> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bill_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getId());
            holder.mContentView.setText(mValues.get(position).getText());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(StudentDetailFragment.BILL_ID, holder.mItem.getId());
                        StudentDetailFragment fragment = new StudentDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.bill_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, StudentDetailActivity.class);
                        intent.putExtra(StudentDetailFragment.BILL_ID, holder.mItem.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Student mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
