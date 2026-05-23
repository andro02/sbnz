package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ftn.sbnz.model.feature.NearbyFeature;

public class Dumpsite {
    private String id;
    private double areaSqm;
    private Date detectedAt;
    private double temperature;
    private GeoPoint location;
    private List<NearbyFeature> nearbyFeatures = new ArrayList<>();

    // flags set by rules
    private boolean waterRiskFlag = false;
    private boolean accessPossible = false;
    private boolean fireRisk = false;
    private List<String> threatenedWaterBodies = new ArrayList<>();
    private List<String> affectedPopulations = new ArrayList<>();

    private RiskAssessment riskAssessment = new RiskAssessment();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAreaSqm() {
        return areaSqm;
    }

    public void setAreaSqm(double areaSqm) {
        this.areaSqm = areaSqm;
    }

    public Date getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(Date detectedAt) {
        this.detectedAt = detectedAt;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public List<NearbyFeature> getNearbyFeatures() {
        return nearbyFeatures;
    }

    public void setNearbyFeatures(List<NearbyFeature> nearbyFeatures) {
        this.nearbyFeatures = nearbyFeatures;
    }

    public boolean isWaterRiskFlag() {
        return waterRiskFlag;
    }

    public void setWaterRiskFlag(boolean waterRiskFlag) {
        this.waterRiskFlag = waterRiskFlag;
    }

    public boolean isAccessPossible() {
        return accessPossible;
    }

    public void setAccessPossible(boolean accessPossible) {
        this.accessPossible = accessPossible;
    }

    public boolean isFireRisk() {
        return fireRisk;
    }

    public void setFireRisk(boolean fireRisk) {
        this.fireRisk = fireRisk;
    }

    public List<String> getThreatenedWaterBodies() {
        return threatenedWaterBodies;
    }

    public void setThreatenedWaterBodies(List<String> threatenedWaterBodies) {
        this.threatenedWaterBodies = threatenedWaterBodies;
    }

    public List<String> getAffectedPopulations() {
        return affectedPopulations;
    }

    public void setAffectedPopulations(List<String> affectedPopulations) {
        this.affectedPopulations = affectedPopulations;
    }

    public RiskAssessment getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(RiskAssessment riskAssessment) {
        this.riskAssessment = riskAssessment;
    }
}
