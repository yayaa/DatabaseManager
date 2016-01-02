package com.yayandroid.databasemanager.utility;

import android.database.Cursor;

import com.yayandroid.databasemanager.annotation.ColumnName;
import com.yayandroid.databasemanager.listener.QueryListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Yahya Bayramoglu on 02/01/16.
 */
public class InjectionManager<T> {

    private QueryListener listener;
    private Class<T> clazz;
    private Field[] fields;
    private String[] columnNames;
    private Cursor cursor;
    private boolean manualInjectionIsOn = true;

    public InjectionManager(Class<T> clazz, QueryListener listener, Cursor cursor) {
        this.listener = listener;
        this.clazz = clazz;
        this.cursor = cursor;
    }

    public ArrayList<T> getListFromCursor() {
        ArrayList<T> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    T item = clazz.newInstance();

                    boolean objectHandledManually = false;
                    if (manualInjectionIsOn && listener != null) {
                        objectHandledManually = listener.manualInjection(item, cursor);
                    }

                    if (!objectHandledManually) {
                        manualInjectionIsOn = false;
                        deserializeInto(item, cursor);
                    }

                    list.add(item);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    public void deserializeInto(T item, Cursor cursor) {
        Field[] fields = getFields(item);
        String[] columnNames = getColumnNames(cursor);
        for (int i = 0; i < columnNames.length; i++) {
            for (int j = 0; j < fields.length; j++) {
                if (compareFieldColumn(fields[j], columnNames[i])) {
                    try {
                        fields[j].setAccessible(true);
                        fields[j].set(item, cursor.getString(i));
                        break;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean compareFieldColumn(Field field, String columnName) {
        if (field.getName().equals(columnName)) {
            return true;
        }

        ColumnName annotation = field.getAnnotation(ColumnName.class);
        if (annotation == null)
            return false;
        else
            return annotation.value().equals(columnName);
    }

    private Field[] getFields(T item) {
        if (fields == null) {
            fields = item.getClass().getDeclaredFields();
        }
        return fields;
    }

    private String[] getColumnNames(Cursor cursor) {
        if (columnNames == null) {
            columnNames = cursor.getColumnNames();
        }
        return columnNames;
    }

}