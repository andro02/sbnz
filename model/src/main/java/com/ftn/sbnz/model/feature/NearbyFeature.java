package com.ftn.sbnz.model.feature;

public abstract class NearbyFeature {
    protected String name;
    protected double distanceM;

    public NearbyFeature(String name, double distanceM) {
        this.name = name;
        this.distanceM = distanceM;
    }

    public abstract double computeRisk();

    public String getName() { return name; }
    public double getDistanceM() { return distanceM; }
}