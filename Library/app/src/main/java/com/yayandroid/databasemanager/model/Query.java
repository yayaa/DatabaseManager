package com.yayandroid.databasemanager.model;

import com.yayandroid.databasemanager.listener.QueryListener;

/**
 * Created by Yahya Bayramoglu on 21.10.2015.
 */
public class Query {

    private String databaseTag;
    private String query;
    private String[] args;
    private QueryListener listener;

    public Query(String from) {
        this.databaseTag = from;
    }

    /**
     * SELECT, DELETE, UPDATE, INSERT, and so on... All query formats,
     * which are supported by SqlLite, are possible to use. But be aware,
     * you have to put ? in place of variables and set them via {@link Query#withArgs(String...)}
     *
     * @param query Any executable query as in string format
     */
    public Query set(String query) {
        this.query = query;
        return this;
    }

    /**
     * Insert method has been covered specially, because it is mostly same
     *
     * @param tableName   Which table you want to insert values
     * @param columnCount Column count to determine how many ? is required
     */
    public Query insert(String tableName, int columnCount) {
        this.query = "INSERT INTO " + tableName + " VALUES (" + getPlaceHolders(columnCount) + ")";
        return this;
    }

    /**
     * Any query, which requires variables, needs to call this method
     * with args by exact ? count and in exact order declared in query string
     *
     * @param args Required variables in query string
     */
    public Query withArgs(String... args) {
        this.args = args;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                args[i] = "";
            }
        }
        return this;
    }

    /**
     * Set a listener to get notified whenever database process ends with this query object
     */
    public Query setListener(QueryListener listener) {
        this.listener = listener;
        return this;
    }

    public String getDatabaseTag() {
        return databaseTag;
    }

    public String getQuery() {
        return query;
    }

    public String[] getArgs() {
        return args;
    }

    public QueryListener getListener() {
        return listener;
    }

    private String getPlaceHolders(int count) {
        String placeHolder = "";
        for (int i = 0; i < count; i++) {
            placeHolder += " ?,";
        }
        placeHolder = placeHolder.substring(0, placeHolder.length() - 1);
        return placeHolder;
    }

}