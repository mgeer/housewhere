package com.sjl.housewhere.model;

public class Estate {

    private String name;
    private double price;
    private int area;
    private double longitude;
    private double latitude;

    public Estate(String name, double price, int area, double longitude, double latitude) {
        this.name = name;
        this.price = price;
        this.area = area;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getArea() {
        return area;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public String toString() {
        return String.format("%s/%s/%s/%s/%s", name, price, area, longitude, latitude);
    }
}
