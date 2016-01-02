package com.yayandroid.databasemanager.listener;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import com.yayandroid.databasemanager.model.Query;
import com.yayandroid.databasemanager.utility.InjectionManager;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

/**
 * Created by Yahya Bayramoglu on 29/12/15.
 */
public abstract class QueryListener<T> {

    private boolean returnAsList = false;
    private Class<T> type;

    public QueryListener() {
        // returnAsList will be false as default
    }

    public QueryListener(boolean returnAsList) {
        this.returnAsList = returnAsList;
    }

    /**
     * This method will be called as soon as query has completed, even after
     * {@link QueryListener#noDataFound()} || {@link QueryListener#onSingleItemReceived(Object)} ||
     * {@link QueryListener#onListReceived(ArrayList)} methods.
     */
    public void onComplete(Query query) {

    }

    /**
     * This method will be called only with SELECT queries and if there are more than one item
     * corresponding the given select query. If there is only one item and returnAsList flag
     * is not true then it would call {@link QueryListener#onSingleItemReceived(Object)}
     */
    public void onListReceived(ArrayList<T> result) {

    }

    /**
     * This method will be called only when a SELECT query gets single item as result
     * and while returnAsList flag is false
     */
    public void onSingleItemReceived(T result) {

    }

    /**
     * This method will be called only with SELECT queries when there is no item corresponding it
     */
    public void noDataFound() {

    }

    /**
     * This method is provided just because if you don't want to use reflection
     * to set your custom objects in concern of performance
     *
     * @param object Current object which needs to be set its params
     * @param cursor Current cursor object to get values and set it to given object
     * @return True if you handle, false otherwise. And if you return false,
     * this method will not be called again for the running query
     */
    public boolean manualInjection(T object, Cursor cursor) {
        return false;
    }

    /**
     * Do not override unless you know what to do!
     */
    public void onCursorReceived(final Query query, Cursor cursor) {
        final ArrayList<T> list = new InjectionManager<T>(getClassByType(), this, cursor).getListFromCursor();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (list.size() == 0) {
                    noDataFound();
                } else {
                    if (!returnAsList && list.size() == 1) {
                        onSingleItemReceived(list.get(0));
                    } else {
                        onListReceived(list);
                    }
                }
                onComplete(query);
            }
        });
    }

    private Class<T> getClassByType() {
        if (type == null) {
            this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return type;
    }

}