package com.ftn.sbnz.model.drools;

public class GeoPoint {
    private double lat;
    private double lon;

    public GeoPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double distanceTo(GeoPoint other) {
        final int R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(other.lat - this.lat);
        double dLon = Math.toRadians(other.lon - this.lon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(this.lat))
                 * Math.cos(Math.toRadians(other.lat))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    @Override
    public String toString() {
        return "GeoPoint(" + lat + ", " + lon + ")";
    }
}