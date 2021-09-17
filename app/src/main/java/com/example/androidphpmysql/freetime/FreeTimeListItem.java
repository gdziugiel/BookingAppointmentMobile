package com.example.androidphpmysql.freetime;

public class FreeTimeListItem {
    private final int id;
    private final String timeStart;
    private final String timeEnd;
    private final boolean allDay;

    public FreeTimeListItem(int id, String timeStart, String timeEnd, boolean allDay) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.allDay = allDay;
    }

    public int getId() {
        return id;
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
