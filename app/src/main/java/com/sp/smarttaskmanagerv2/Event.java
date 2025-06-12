package com.sp.smarttaskmanagerv2;

public class Event {
    private long id;
    private String title;
    private long startTime;

    public Event(long id, String title, long startTime) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getStartTime() {
        return startTime;
    }
}