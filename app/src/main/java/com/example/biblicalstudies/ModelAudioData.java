package com.example.biblicalstudies;


public class ModelAudioData {

    private String title;
    private String path;
    private String uploadTime;
    private boolean isLocked;
    private String lockId;
    private String language;

    public String getLanguage() {
        return language;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public String getLockId() {
        return lockId;
    }

    public ModelAudioData() {
    }

    public ModelAudioData(String title, String path, String uploadTime) {
        this.title = title;
        this.path = path;
        this.uploadTime = uploadTime;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getUploadTime() {
        return uploadTime;
    }
}
