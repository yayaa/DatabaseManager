package com.yayandroid.databasemanager.sample.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.yayandroid.databasemanager.listener.QueryListener;
import com.yayandroid.databasemanager.model.Query;
import com.yayandroid.databasemanager.sample.Constants;
import com.yayandroid.databasemanager.sample.R;
import com.yayandroid.databasemanager.sample.model.Ticket;

import java.util.ArrayList;

/**
 * Created by Yahya Bayramoglu on 31/12/15.
 */
public class MainActivity extends BaseActivity {

    private CheckBox reflectionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reflectionCheck = (CheckBox) findViewById(R.id.useReflectionCheck);
        reflectionCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setUseReflection(isChecked);
            }
        });
    }

    public void databaseClick(View v) {
        String databaseTag = "";
        switch (v.getId()) {
            case R.id.dbTextDisc: {
                databaseTag = Constants.DB_TAG_DISC;
                break;
            }
            case R.id.dbTextAssets: {
                databaseTag = Constants.DB_TAG_ASSETS;
                break;
            }
            case R.id.dbTextLocal: {
                databaseTag = Constants.DB_TAG_LOCAL;
                break;
            }
        }

        Intent intent = new Intent(this, DBListActivity.class);
        intent.putExtra(Constants.INTENT_KEY_TAG, databaseTag);
        startActivity(intent);
    }

    public void compareClick(View v) {
        displayProgress();
        Query combinedComparisonQuery = new Query(Constants.DB_TAG_DISC)
                .set(Constants.COMPARISON_QUERY)
                .setListener(comparisonListener);
        getDatabaseManager().selectByMerged(combinedComparisonQuery, Constants.DB_TAG_ASSETS);
    }

    private QueryListener<Ticket> comparisonListener = new QueryListener<Ticket>(true) {

        @Override
        public boolean manualInjection(Ticket object, Cursor cursor) {
            if (getUseReflection()) {
                return false;
            } else {
                object.setId(cursor.getString(0));
                object.setDate(cursor.getString(1));
                return true;
            }
        }

        @Override
        public void onListReceived(ArrayList<Ticket> result) {
            dismissProgress();

            Intent intent = new Intent(MainActivity.this, DBListActivity.class);
            intent.putParcelableArrayListExtra(Constants.INTENT_KEY_LIST, result);
            startActivity(intent);
        }

        @Override
        public void noDataFound() {
            dismissProgress();
            Toast.makeText(getApplicationContext(), R.string.toast_no_difference, Toast.LENGTH_SHORT).show();
        }

    };

}