package com.yayandroid.databasemanager.sample.helper;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Yahya Bayramoglu on 31/12/15.
 */
public class DbOpenHelperAssets extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "tickets_assets.s3db";
    private static final int DATABASE_VERSION = 1;

    public DbOpenHelperAssets(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}