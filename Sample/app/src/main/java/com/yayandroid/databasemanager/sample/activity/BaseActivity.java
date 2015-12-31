package com.yayandroid.databasemanager.sample.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.yayandroid.databasemanager.DatabaseManager;
import com.yayandroid.databasemanager.sample.SampleApplication;

/**
 * Created by Yahya Bayramoglu on 30/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private final boolean PROGRESS_CANCELABLE = false;

    protected DatabaseManager getDatabaseManager() {
        return ((SampleApplication) getApplication()).getDatabaseManager();
    }

    public void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        progressDialog.setMessage("Progress...");
        progressDialog.setCancelable(PROGRESS_CANCELABLE);
        progressDialog.setCanceledOnTouchOutside(PROGRESS_CANCELABLE);

        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}