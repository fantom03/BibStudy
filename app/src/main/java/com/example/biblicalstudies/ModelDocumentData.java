package com.example.biblicalstudies;

public class ModelDocumentData {

    private String name;
    private String path;
    private boolean isLocked;
    private long timeStamp;
    private String lockId;
    private String language;

    public String getLanguage() {
        return language;
    }

    public String getLockId() {
        return lockId;
    }

    public ModelDocumentData() {
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}