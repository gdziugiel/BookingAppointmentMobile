package com.example.androidphpmysql.time;

public class ExcludingTimeListItem {
    private final String date;
    private final String timeStart;
    private final String timeEnd;
    private final boolean allDay;

    public ExcludingTimeListItem(String date, String timeStart, String timeEnd, boolean allDay) {
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.allDay = allDay;
    }

    public String getDate() {
        return date;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public boolean isAllDay() {
        return allDay;
    }
}
