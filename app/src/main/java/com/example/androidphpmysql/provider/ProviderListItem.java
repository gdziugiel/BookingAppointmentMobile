package com.example.androidphpmysql.provider;

public class ProviderListItem {
    private final int id;
    private final String providerFirstname;
    private final String providerLastname;
    private final String email;

    public ProviderListItem(int id, String providerFirstname, String providerLastname, String email) {
        this.id = id;
        this.providerFirstname = providerFirstname;
        this.providerLastname = providerLastname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getProviderFirstname() {
        return providerFirstname;
    }

    public String getProviderLastname() {
        return providerLastname;
    }

    public String getEmail() {
        return email;
    }
}
