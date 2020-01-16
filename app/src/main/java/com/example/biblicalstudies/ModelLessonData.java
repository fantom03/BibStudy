package com.example.biblicalstudies;

import java.util.Map;

public class ModelLessonData {

    private Map<String, Boolean> docs;
    private boolean isLocked;
    private long timeStamp;
    private String name;

    public ModelLessonData() {
    }

    public Map<String, Boolean> getDocs() {
        return docs;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getName() {
        return name;
    }
}
