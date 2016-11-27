package com.studentApp.content;

/**
 * Created by Rares on 27.11.2016.
 */


public class Student {

    public enum Status {
        active,
        archived;

    }
    private String mId;
    private String mUserId;
    private String mText;
    private Status mStatus = Status.active;
    private long mUpdated;
    private int mVersion;

    public Student() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }



    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public long getUpdated() {
        return mUpdated;
    }

    public void setUpdated(long updated) {
        mUpdated = updated;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        mVersion = version;
    }

    @Override
    public String toString() {
        return "Student{" +
                "mId='" + mId + '\'' +
                ", mUserId='" + mUserId + '\'' +
                ", mText='" + mText + '\'' +
                ", mPrice='" + mPrice + '\'' +
                ", mStatus=" + mStatus +
                ", mUpdated=" + mUpdated +
                ", mVersion=" + mVersion +
                '}';
    }

