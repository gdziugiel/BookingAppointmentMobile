package com.example.androidphpmysql.time;

public class WorkTimeListItem {
    private final int id;
    private final int day;
    private final String timeStart;
    private final String timeEnd;

    public WorkTimeListItem(int id, int day, String timeStart, String timeEnd) {
        this.id = id;
        this.day = day;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public int getId() {
        return id;
    }

    public int getDay() {
        return day;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }
}
