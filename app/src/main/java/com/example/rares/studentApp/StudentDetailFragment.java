package com.studentApp.;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.studentApp.App;
import com.studentApp.content.Bill;
import com.studentApp.content.Student;
import com.studentApp.util.Cancellable;
import com.studentApp.util.DialogUtils;
import com.studentApp.util.OnErrorListener;
import com.studentApp.util.OnSuccessListener;

/**
 * A fragment representing a single Bill detail screen.
 * This fragment is either contained in a {@link StudentListActivity}
 * in two-pane mode (on tablets) or a {@link StudentDetailActivity}
 * on handsets.
 */
public class StudentDetailFragment extends Fragment {
    public static final String TAG = StudentDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String BILL_ID = "bill_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Student mStudent;

    private App mApp;

    private Cancellable mFetchBillAsync;
    private EditText mBillTextView;
    private CollapsingToolbarLayout mAppBarLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StudentDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        mApp = (App) context.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(BILL_ID)) {
            // In a real-world scenario, use a Loader
            // to load content from a content provider.
            Activity activity = this.getActivity();
            mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.bill_detail, container, false);
        mBillTextView = (EditText) rootView.findViewById(R.id.bill_text);
        fillBillDetails();
        fetchBillAsync();
        return rootView;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void fetchBillAsync() {
        mFetchStudentAsync = mApp.getStudentManager().getStudentAsync(
                getArguments().getString(BILL_ID),
                new OnSuccessListener<Student>() {

                    @Override
                    public void onSuccess(final Student student) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStudent = student;
                                fillStudentDetails();


                            }
                        });
                    }
                }, new OnErrorListener() {

                    @Override
                    public void onError(final Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.showError(getActivity(), e);
                            }
                        });
                    }
                });
    }
    private void fillStudentDetails() {
        if (mStudent != null) {
            if (mAppBarLayout != null) {
                mAppBarLayout.setTitle(mStudent.getText());
            }
            mBillTextView.setText(mStudent.getText());
            mBillTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EditText et= (EditText) v.findViewById(R.id.bill_text);
                    Log.d(TAG, "edit");
                    String str=et.getText().toString();
                    //mStudentTextView.setText("aaa");
                    String a="";
                    //edit(et);
                    Snackbar.make(v, "Authenticating, please wait", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Action", null).show();

                    return false;
                }
            });
            mBillTextView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    return true;
                }
            });
        }
    }
    public void edit(EditText et){
        Log.d(TAG, "edit");
        String str=et.getText().toString();
        //mBillTextView.setText("aaa");
        String a="";
    }
    @Override
    public void onDetach(){
        super.onDetach();
    }
}
