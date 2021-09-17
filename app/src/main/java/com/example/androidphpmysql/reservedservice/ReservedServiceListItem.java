package com.example.androidphpmysql.reservedservice;

public class ReservedServiceListItem {
    private final int id;
    private final String name;
    private final String time;
    private final String email;

    public ReservedServiceListItem(int id, String name, String time, String email) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getEmail() {
        return email;
    }
}