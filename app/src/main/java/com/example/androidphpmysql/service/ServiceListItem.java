package com.example.androidphpmysql.service;

public class ServiceListItem {
    private final int id;
    private final String name;
    private final String description;
    private final String providerFirstname;
    private final String providerLastname;
    private final String category;
    private final String city;

    public ServiceListItem(int id, String name, String description, String providerFirstname, String providerLastname, String category, String city) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.providerFirstname = providerFirstname;
        this.providerLastname = providerLastname;
        this.category = category;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getProviderFirstname() {
        return providerFirstname;
    }

    public String getProviderLastname() {
        return providerLastname;
    }

    public String getCategory() {
        return category;
    }

    public String getCity() {
        return city;
    }
}
