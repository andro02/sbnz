package com.ftn.sbnz.model.drools;

public class RiskAssessment {
    private double riverRisk;
    private double lakeRisk;
    private double urbanRisk;
    private double sizeRisk;
    private double roadRisk;
    private double industryRisk;
    private double sensPopRisk;
    private double accessibilityPenalty;
    private double totalRisk;
    private RiskLevel riskLevel;
    private boolean waterAggregated = false;
    private boolean riverCalculated = false;
    private boolean roadCalculated = false;

    public void computeTotal() {
        this.totalRisk = riverRisk + lakeRisk + urbanRisk + sizeRisk
                + roadRisk + industryRisk + sensPopRisk + accessibilityPenalty;
    }

    public double getRiverRisk() {
        return riverRisk;
    }

    public void setRiverRisk(double riverRisk) {
        this.riverRisk = riverRisk;
    }

    public double getLakeRisk() {
        return lakeRisk;
    }

    public void setLakeRisk(double lakeRisk) {
        this.lakeRisk = lakeRisk;
    }

    public double getUrbanRisk() {
        return urbanRisk;
    }

    public void setUrbanRisk(double urbanRisk) {
        this.urbanRisk = urbanRisk;
    }

    public double getSizeRisk() {
        return sizeRisk;
    }

    public void setSizeRisk(double sizeRisk) {
        this.sizeRisk = sizeRisk;
    }

    public double getRoadRisk() {
        return roadRisk;
    }

    public void setRoadRisk(double roadRisk) {
        this.roadRisk = roadRisk;
    }

    public double getIndustryRisk() {
        return industryRisk;
    }

    public void setIndustryRisk(double industryRisk) {
        this.industryRisk = industryRisk;
    }

    public double getSensPopRisk() {
        return sensPopRisk;
    }

    public void setSensPopRisk(double sensPopRisk) {
        this.sensPopRisk = sensPopRisk;
    }

    public double getAccessibilityPenalty() {
        return accessibilityPenalty;
    }

    public void setAccessibilityPenalty(double accessibilityPenalty) {
        this.accessibilityPenalty = accessibilityPenalty;
    }

    public double getTotalRisk() {
        return totalRisk;
    }

    public void setTotalRisk(double totalRisk) {
        this.totalRisk = totalRisk;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public boolean isWaterAggregated() {
        return waterAggregated;
    }

    public void setWaterAggregated(boolean waterAggregated) {
        this.waterAggregated = waterAggregated;
    }

    public boolean isRiverCalculated() {
        return riverCalculated;
    }

    public void setRiverCalculated(boolean riverCalculated) {
        this.riverCalculated = riverCalculated;
    }

    public boolean isRoadCalculated() {
        return roadCalculated;
    }

    public void setRoadCalculated(boolean roadCalculated) {
        this.roadCalculated = roadCalculated;
    }
}