package com.example.test.retrofit.data;

public class CoordinateData {
    int empSeq;
    double latitude;
    double longitude;
    String coordType;

    boolean status;
    String message;

    public CoordinateData(int empSeq, double latitude, double longitude, String coordType) {
        this.empSeq = empSeq;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coordType = coordType;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
