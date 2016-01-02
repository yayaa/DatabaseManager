package com.yayandroid.databasemanager.sample.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.yayandroid.databasemanager.annotation.ColumnName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yahya Bayramoglu on 30/12/15.
 */
public class Ticket implements Parcelable {

    @ColumnName("TicketId")
    private String id;

    @ColumnName("CreateDate")
    private String date;

    public Ticket() {
        // Required by library to create a newInstance of this class
    }

    public Ticket(String id, String date) {
        this.id = id;
        this.date = date;
    }

    public Ticket(Parcel in) {
        this.id = in.readString();
        this.date = in.readString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public static Ticket createRandom(int id) {
        Date date = new Date();
        String createdDate = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).format(date);
        return new Ticket("ticketID_" + id, createdDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(date);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

}