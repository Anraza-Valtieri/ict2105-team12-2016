package com.example.chowdi.qremind.infrastructure;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Queue;

/**
 * Created by anton on 31/3/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueInfo implements Parcelable {

    private Boolean calling;
    private Boolean connected;
    private String current_location;
    private String customer_id;
    private String in_queue_date;
    private String in_queue_time;
    private int queue_no;
    private Boolean time_ext_requested;
    private int waiting_int;
    private String queue_key;

    public QueueInfo()
    {

    }

    protected QueueInfo(Parcel in) {
        current_location = in.readString();
        customer_id = in.readString();
        in_queue_date = in.readString();
        in_queue_time = in.readString();
        queue_no = in.readInt();
        waiting_int = in.readInt();
        queue_key = in.readString();
    }

    public static final Creator<QueueInfo> CREATOR = new Creator<QueueInfo>() {
        @Override
        public QueueInfo createFromParcel(Parcel in) {
            return new QueueInfo(in);
        }

        @Override
        public QueueInfo[] newArray(int size) {
            return new QueueInfo[size];
        }
    };

    public Boolean getCalling() {
        return calling;
    }

    public void setCalling(Boolean calling) {
        this.calling = calling;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getIn_queue_date() {
        return in_queue_date;
    }

    public void setIn_queue_date(String in_queue_date) {
        this.in_queue_date = in_queue_date;
    }

    public String getIn_queue_time() {
        return in_queue_time;
    }

    public void setIn_queue_time(String in_queue_time) {
        this.in_queue_time = in_queue_time;
    }

    public int getQueue_no() {
        return queue_no;
    }

    public void setQueue_no(int queue_no) {
        this.queue_no = queue_no;
    }

    public Boolean getTime_ext_requested() {
        return time_ext_requested;
    }

    public void setTime_ext_requested(Boolean time_ext_requested) {
        this.time_ext_requested = time_ext_requested;
    }

    public int getWaiting_int() {
        return waiting_int;
    }

    public void setWaiting_int(int waiting_int) {
        this.waiting_int = waiting_int;
    }

    public String getQueue_key() {
        return queue_key;
    }

    public void setQueue_key(String queue_key) {
        this.queue_key = queue_key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(current_location);
        dest.writeString(customer_id);
        dest.writeString(in_queue_date);
        dest.writeString(in_queue_time);
        dest.writeInt(queue_no);
        dest.writeInt(waiting_int);
        dest.writeString(queue_key);
    }
}
