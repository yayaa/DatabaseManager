package com.yayandroid.databasemanager.sample;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.yayandroid.databasemanager.DatabaseManager;
import com.yayandroid.databasemanager.model.Database;
import com.yayandroid.databasemanager.sample.helper.DbOpenHelperAssets;
import com.yayandroid.databasemanager.sample.helper.DbOpenHelperLocal;

import java.io.File;

/**
 * Created by Yahya Bayramoglu on 30/12/15.
 */
public class SampleApplication extends Application {

    private DatabaseManager dbManager;

    @Override
    public void onCreate() {
        super.onCreate();

        dbManager = new DatabaseManager();

        // Add local database to manager
        dbManager.addDatabase(new Database.Builder(Database.LOCAL, Constants.DB_TAG_LOCAL)
                .openWith(new DbOpenHelperLocal(getApplicationContext()))
                .build());

        // Add database from assets folder, because we will be providing an openHelper
        // it is fine to use Database.LOCAL type
        dbManager.addDatabase(new Database.Builder(Database.LOCAL, Constants.DB_TAG_ASSETS)
                .openWith(new DbOpenHelperAssets(getApplicationContext()))
                .path(getApplicationInfo().dataDir + "/databases/tickets_assets.s3db")
                .build());

        // Add database from disc
        // TODO: Please place the given tickets_disc.s3db file into your root folder
        // So the sample application reach that out, otherwise it will crash!
        dbManager.addDatabase(new Database.Builder(Database.DISC, Constants.DB_TAG_DISC)
                .path(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tickets_disc.s3db")
                .openFlags(SQLiteDatabase.OPEN_READONLY)
                .build());
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

}