package com.yayandroid.databasemanager.model;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.yayandroid.databasemanager.DatabaseManager;

import java.io.File;

/**
 * Created by Yahya Bayramoglu on 29/12/15.
 */
public class Database {

    private int openFlags;
    private int type;
    private String path;
    private String tag;
    private SQLiteDatabase database;
    private SQLiteOpenHelper openHelper;

    /**
     * Database is stored in external / internal storage
     */
    public static final int DISC = 1;

    /**
     * Database is stored in application's database folder,
     * and will be reached out by provided SQLiteOpenHelper
     */
    public static final int LOCAL = 2;

    private Database(Builder builder) {
        this.type = builder.type;
        if (type != DISC && type != LOCAL) {
            throw new IllegalArgumentException("Not acceptable database type. Please use one of DISC or LOCAL types.");
        }

        this.path = builder.path;
        this.tag = builder.tag;
        this.openFlags = builder.openFlags;
        this.openHelper = builder.openHelper;
        this.database = builder.database;
    }

    public String getTag() {
        return tag;
    }

    public String getPath() {
        return path;
    }

    public boolean isReadOnly() {
        return openFlags == SQLiteDatabase.OPEN_READONLY;
    }

    public SQLiteDatabase get() {
        if (!isOpen()) {
            // If there is an instance but somehow not accessible, close that first
            close();
            database = null;

            switch (type) {
                case DISC: {
                    if (TextUtils.isEmpty(path)) {
                        throw new RuntimeException("You have to specify the required database path in storage");
                    }

                    if (exists()) {
                        database = SQLiteDatabase.openDatabase(path, null, openFlags);
                    } else {
                        throw new NullPointerException("No Database found in path: " + path);
                    }

                    break;
                }
                case LOCAL: {
                    if (openHelper == null) {
                        throw new RuntimeException("You have to specify a SQLiteOpenHelper class in order to use LOCAL database");
                    }

                    if (openFlags == SQLiteDatabase.OPEN_READONLY) {
                        database = openHelper.getReadableDatabase();
                    } else if (openFlags == SQLiteDatabase.OPEN_READWRITE) {
                        database = openHelper.getWritableDatabase();
                    } else {
                        throw new IllegalArgumentException("Defined openFlags are not acceptable. Please use one of SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.OPEN_READWRITE");
                    }

                    break;
                }
            }
        }
        return database;
    }

    /**
     * To close database instance manually
     */
    public void close() {
        if (database != null) {
            database.close();
        }
    }

    private boolean isOpen() {
        return database != null && database.isOpen();
    }

    private boolean exists() {
        File file = new File(path);
        if (file.exists()) {
            Log.i("DatabaseManager", "Database found in path: " + path);
            return true;
        } else {
            return false;
        }
    }

    public static class Builder {

        private int openFlags = SQLiteDatabase.OPEN_READWRITE;
        private int type;
        private String path;
        private String tag;
        private SQLiteDatabase database;
        private SQLiteOpenHelper openHelper;

        /**
         * @param databaseTag Needs to be unique because it will be used in DatabaseManager
         *                    to figure out which is target database and if you try to add
         *                    multiple databases with same tag it will replace
         * @param type        Declare where the required database is stored
         *                    {@link Database#DISC} || {@link Database#LOCAL}
         */
        public Builder(int type, String databaseTag) {
            this.type = type;
            this.tag = databaseTag;
        }

        /**
         * @param path full path of the required database
         *             if type is LOCAL normally no need to set, but if you need to use
         *             {@link DatabaseManager#selectByMerged(Query, String)} or derivatives
         *             then you have to specify full path as well, because manager will use this
         *             to reach out required database to merge within given query.
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * If the required database is in LOCAL type
         * then you have to specify your custom SQLiteOpenHelper class
         */
        public <T extends SQLiteOpenHelper> Builder openWith(T helper) {
            this.openHelper = helper;
            return this;
        }

        /**
         * This will be used to determine the required database object needs to open as readOnly
         * or writable as well. It accepts {@link SQLiteDatabase#OPEN_READONLY} ||
         * {@link SQLiteDatabase#OPEN_READWRITE} while READWRITE as default
         */
        public Builder openFlags(int flags) {
            this.openFlags = flags;
            return this;
        }

        /**
         * Do not use this method unless you have to!
         */
        public Builder asDatabase(SQLiteDatabase database) {
            /**
             * For instance, if you have your database in assets folder,
             * then you can use SQLite AssetsHelper library and get database out of that
             * to pass here so you can still use DatabaseManager.
             *
             * https://github.com/jgilfelt/android-sqlite-asset-helper
             */
            this.database = database;
            return this;
        }

        public Database build() {
            return new Database(this);
        }

    }

}