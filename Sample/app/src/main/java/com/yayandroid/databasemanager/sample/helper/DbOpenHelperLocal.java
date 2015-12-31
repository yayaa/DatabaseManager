package com.yayandroid.databasemanager.sample.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yayandroid.databasemanager.sample.Constants;

/**
 * Created by Yahya Bayramoglu on 30.12.2015.
 */
public class DbOpenHelperLocal extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tickets_local.db";
    private static final int DATABASE_VERSION = 1;

    private final String TABLE_CREATE_LISTING = "CREATE TABLE "
            + Constants.TABLE_TICKET + " ( \"TicketId\" TEXT, \"CreateDate\" TEXT )";

    public DbOpenHelperLocal(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_LISTING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * This will be needed only when table's columns have changed by version
         * and this is up to DATABASE_VERSION code
         */
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE_LISTING);
        // onCreate(db);
    }

}