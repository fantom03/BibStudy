package com.example.biblicalstudies;

import java.util.Map;

public class ModelLessonData {

    private Map<String, Boolean> docs;
    private boolean isLocked;
    private long timeStamp;
    private String name;
    private String lockId;

    public ModelLessonData() {
    }

    public void setDocs(Map<String, Boolean> docs) {
        this.docs = docs;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public Map<String, Boolean> getDocs() {
        return docs;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getName() {
        return name;
    }
}
