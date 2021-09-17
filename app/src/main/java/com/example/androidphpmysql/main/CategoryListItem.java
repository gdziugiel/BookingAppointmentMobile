package com.example.androidphpmysql.main;

public class CategoryListItem {
    private final int id;
    private final String name;

    public CategoryListItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
