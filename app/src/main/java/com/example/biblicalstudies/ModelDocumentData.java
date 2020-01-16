package com.example.biblicalstudies;

public class ModelDocumentData {

    private String name;
    private String path;
    private boolean isLocked;
    private long timeStamp;

    public ModelDocumentData() {
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}