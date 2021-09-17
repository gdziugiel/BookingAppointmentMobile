package com.example.androidphpmysql.service;

public class WeekdayListItem {
    private final int id;
    private final int workTimeId;
    private final String name;
    private final String timeStart;
    private final String timeEnd;

    public WeekdayListItem(int id, int workTimeId, String name, String timeStart, String timeEnd) {
        this.id = id;
        this.workTimeId = workTimeId;
        this.name = name;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public int getId() {
        return id;
    }

    public int getWorkTimeId() {
        return workTimeId;
    }

    public String getName() {
        return name;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }
}
