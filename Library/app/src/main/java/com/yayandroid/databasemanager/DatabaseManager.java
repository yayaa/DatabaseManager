package com.yayandroid.databasemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yayandroid.databasemanager.model.Database;
import com.yayandroid.databasemanager.model.Query;
import com.yayandroid.databasemanager.utility.InjectionManager;

import java.util.ArrayList;

/**
 * Create a single instance of DatabaseManager, most eligible way in application class,
 * and reach out that instance to interact. This manager doesn't have a static singleton instance
 * to prevent having memory leak while keeping database instances which has attached to contexts.
 * Please keep it that way.
 * <p/>
 * Created by Yahya Bayramoglu on 29/12/15
 */
public class DatabaseManager {

    private ArrayList<Database> databases;

    public DatabaseManager() {
        this.databases = new ArrayList<>();
    }

    /**
     * Create a Database object and add to the manager so when you declare it from any query
     * it'll find and run query on it. Do not forget to add database
     * before you attempt to run a query on it, otherwise it will crash.
     * <p/>
     * ATTENTION: To prevent having "java.lang.IllegalStateException:
     * SQLiteDatabase created and never closed" it is recommended to create database object
     * with application context, so that it will live as long as application lives and
     * you won't need to close it manually.
     */
    public void addDatabase(Database database) {
        synchronized (databases) {
            // Remove if there is a database with same tag
            for (int i = 0; i < databases.size(); i++) {
                if (databases.get(i).getTag().equals(database.getTag())) {
                    databases.remove(i);
                    break;
                }
            }
            // add given database object
            databases.add(database);
        }
    }

    /**
     * Returns database object if there is one matches with given tag name,
     * if not throws an exception, so ensure you add that database into this manager first
     *
     * @param databaseTag Database tag which is declared in Database object
     * @return Database object with given tagName
     */
    public Database getDatabaseByTag(String databaseTag) {
        synchronized (databases) {
            for (int i = 0; i < databases.size(); i++) {
                if (databases.get(i).getTag().equals(databaseTag)) {
                    return databases.get(i);
                }
            }
        }

        throw new RuntimeException("You have to create your database and add it to databaseManager before attempting to use it.");
    }

    /**
     * Flag to determine database, with given tag name, is in readOnly mode or not
     *
     * @param databaseTag Database tag which is declared in Database object
     * @return True if database is readOnly, false otherwise
     */
    public boolean isReadOnly(String databaseTag) {
        return getDatabaseByTag(databaseTag).isReadOnly();
    }

    /**
     * Call this method to log every table name in given database
     *
     * @param databaseTag Database tag which is declared in Database object
     */
    public void logTablesInDatabase(String databaseTag) {
        String tableQuery = "SELECT name FROM sqlite_master WHERE type='table'";
        Cursor c = getDatabaseByTag(databaseTag).get().rawQuery(tableQuery, null);

        ArrayList<String> tables = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                tables.add(c.getString(0));
            } while (c.moveToNext());
        }

        Log.i("DatabaseManager", "## Tables in " + databaseTag + "##\n" + tables.toString());
    }

    /**
     * Executes a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
     */
    public void execute(final Query query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDatabaseByTag(query.getDatabaseTag()).get().execSQL(query.getQuery(), query.getArgs());
                notifyListenerAsCompleted(query);
            }
        }).start();
    }

    /**
     * SYNC - Executes a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
     */
    public void executeSync(Query query) {
        getDatabaseByTag(query.getDatabaseTag()).get().execSQL(query.getQuery(), query.getArgs());
    }

    /**
     * Executes a SELECT query and returns to queryListener if set
     */
    public void select(final Query query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getDatabaseByTag(query.getDatabaseTag()).get()
                        .rawQuery(query.getQuery(), query.getArgs());
                if (query.getListener() != null) {
                    query.getListener().onCursorReceived(query, cursor);
                }
            }
        }).start();
    }

    /**
     * SYNC - Executes a SELECT query and returns result
     *
     * @param clazz Expected deserialization object
     * @param query Query object which contains necessary data - no need to set listener
     * @return Returns an arrayList which consists of instances of given class
     */
    public <T> ArrayList<T> selectSync(Class<T> clazz, Query query) {
        Cursor cursor = getDatabaseByTag(query.getDatabaseTag()).get().rawQuery(query.getQuery(), query.getArgs());
        return new InjectionManager<T>(clazz, query.getListener(), cursor).getListFromCursor();
    }

    /**
     * Executes a SELECT query which requires to reach out different database. To do that,
     * the other database is also needed to be added into DatabaseManager. And because it will
     * be attached with its tag, while creating query it needs to be used with tag.
     *
     * @param query              Query object which contains necessary data
     * @param databaseTagToMerge Database tag which is required for this query
     */
    public void selectByMerged(final Query query, final String databaseTagToMerge) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database queryDB = getDatabaseByTag(query.getDatabaseTag());
                Database mergeDB = getDatabaseByTag(databaseTagToMerge);

                queryDB.get().execSQL("attach database ? as " + databaseTagToMerge, new String[]{mergeDB.getPath()});

                Cursor cursor = queryDB.get().rawQuery(query.getQuery(), query.getArgs());
                if (query.getListener() != null) {
                    query.getListener().onCursorReceived(query, cursor);
                }

                queryDB.get().execSQL("detach database ?", new String[]{databaseTagToMerge});
            }
        }).start();
    }

    /**
     * SYNC - Executes a SELECT query which requires to reach out different database. To do that,
     * the other database is also needed to be added into DatabaseManager. And because it will
     * be attached with its tag, while creating query it needs to be used with tag.
     *
     * @param clazz              Expected deserialization object
     * @param query              Query object which contains necessary data - no need to set listener
     * @param databaseTagToMerge Database tag which is required for this query
     * @return Returns an arrayList which consists of instances of given class
     */
    public <T> ArrayList<T> selectByMergedSync(Class<T> clazz, Query query, String databaseTagToMerge) {
        Database queryDB = getDatabaseByTag(query.getDatabaseTag());
        Database mergeDB = getDatabaseByTag(databaseTagToMerge);

        queryDB.get().execSQL("attach database ? as " + databaseTagToMerge, new String[]{mergeDB.getPath()});

        ArrayList<T> list = selectSync(clazz, query);

        queryDB.get().execSQL("detach database ?", new String[]{databaseTagToMerge});
        return list;
    }

    /**
     * This method will ensure that data is inserted to database
     * unlikely update method which does nothing if there is no match.
     * <p/>
     * This method has no SYNC version, because you can simply call
     * {@link DatabaseManager#deleteSync(Query)} && {@link DatabaseManager#insertSync(Query)}
     *
     * @param deleteQuery Query object to delete from database
     * @param insertQuery Query object to insert to database
     */
    public void deleteAndInsert(final Query deleteQuery, final Query insertQuery) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteSync(deleteQuery);
                insertSync(insertQuery);
                notifyListenerAsCompleted(insertQuery);
            }
        }).start();
    }

    /**
     * Executes given INSERT statement
     */
    public void insert(final Query query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                insertSync(query);
                notifyListenerAsCompleted(query);
            }
        }).start();
    }

    /**
     * SYNC - Executes given INSERT statement and returns the ID of the row inserted due to this call.
     */
    public long insertSync(Query query) {
        SQLiteStatement insertStatement = getDatabaseByTag(query.getDatabaseTag()).get()
                .compileStatement(query.getQuery());
        insertStatement.bindAllArgsAsStrings(query.getArgs());
        return insertStatement.executeInsert();
    }

    /**
     * Executes given UPDATE statement
     */
    public void update(Query query) {
        updateOrDelete(query);
    }

    /**
     * SYNC - Executes given UPDATE statement and returns the number of rows affected by this SQL statement execution.
     */
    public int updateSync(Query query) {
        return updateOrDeleteSync(query);
    }

    /**
     * Executes given DELETE statement
     */
    public void delete(Query query) {
        updateOrDelete(query);
    }

    /**
     * SYNC - Executes given DELETE statement and returns the number of rows affected by this SQL statement execution.
     */
    public int deleteSync(Query query) {
        return updateOrDeleteSync(query);
    }

    private void updateOrDelete(final Query query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateOrDeleteSync(query);
                notifyListenerAsCompleted(query);
            }
        }).start();
    }

    private int updateOrDeleteSync(Query query) {
        SQLiteStatement updateOrDeleteStatement = getDatabaseByTag(query.getDatabaseTag()).get()
                .compileStatement(query.getQuery());
        updateOrDeleteStatement.bindAllArgsAsStrings(query.getArgs());
        return updateOrDeleteStatement.executeUpdateDelete();
    }

    private void notifyListenerAsCompleted(final Query query) {
        if (query.getListener() != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    query.getListener().onComplete(query);
                }
            });
        }
    }

}