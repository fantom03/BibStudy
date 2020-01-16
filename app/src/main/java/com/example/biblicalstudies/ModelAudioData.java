package com.example.biblicalstudies;

public class ModelAudioData {

    private String title;
    private String path;
    private String uploadTime;

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
