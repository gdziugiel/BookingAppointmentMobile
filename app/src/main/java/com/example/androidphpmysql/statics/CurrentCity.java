package com.example.androidphpmysql.statics;

public class CurrentCity {
    private static double latitude;
    private static double longitude;
    private static int id;
    private static String cityName;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        CurrentCity.id = id;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        CurrentCity.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        CurrentCity.longitude = longitude;
    }

    public static String getCityName() {
        return cityName;
    }

    public static void setCityName(String cityName) {
        CurrentCity.cityName = cityName;
    }

}
