package com.yayandroid.databasemanager.utility;

import android.database.Cursor;
import android.util.Log;

import com.yayandroid.databasemanager.annotation.ColumnName;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Yahya Bayramoglu on 30/12/15.
 */
public class DBMUtils {

    private static final String TAG = "DatabaseManager";

    public static void logI(String message) {
        Log.i(TAG, message);
    }

    public static <T> ArrayList<T> getListFromCursor(Class<T> clazz, Cursor cursor) {
        ArrayList<T> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    T item = clazz.newInstance();
                    deserializeInto(item, cursor);
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

    public static <T> void deserializeInto(T item, Cursor cursor) {
        Field[] fields = item.getClass().getDeclaredFields();
        String[] columnNames = cursor.getColumnNames();
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

    private static boolean compareFieldColumn(Field field, String columnName) {
        if (field.getName().equals(columnName)) {
            return true;
        }

        ColumnName annotation = field.getAnnotation(ColumnName.class);
        if (annotation == null)
            return false;
        else
            return annotation.value().equals(columnName);
    }

}