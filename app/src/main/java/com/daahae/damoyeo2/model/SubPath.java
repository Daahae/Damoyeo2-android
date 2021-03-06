package com.daahae.damoyeo2.model;

import com.google.gson.annotations.SerializedName;

public class SubPath {

    @SerializedName("subwayCode")
    private int subwayCode;
    @SerializedName("busType")
    private int busType;
    @SerializedName("trafficType")
    private int trafficType;
    @SerializedName("sectionTime")
    private int sectionTime;
    @SerializedName("transportNumber")
    private String transportNumber;
    @SerializedName("startStation")
    private String startStation;
    @SerializedName("endStation")
    private String endStation;

    public int getSubwayCode() {
        return subwayCode;
    }

    public int getBusType() {
        return busType;
    }

    public int getTrafficType() {
        return trafficType;
    }

    public int getSectionTime() {
        return sectionTime;
    }

    public String getTransportNumber() {
        return transportNumber;
    }

    public String getStartStation() {
        return startStation;
    }

    public String getEndStation() {
        return endStation;
    }
}
