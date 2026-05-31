package com.ftn.sbnz.model.feature;

public class IndustrialZone extends NearbyFeature {
    private String zoneType;
    private int hazardLevel;

    public IndustrialZone(String name, double distanceM, String zoneType, int hazardLevel) {
        super(name, distanceM);
        this.zoneType = zoneType;
        this.hazardLevel = hazardLevel;
    }

    @Override
    public double computeRisk() {
        return 0.9 * (1.0 / (1 + distanceM / 500.0));
    }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }
    public int getHazardLevel() { return hazardLevel; }
    public void setHazardLevel(int hazardLevel) { this.hazardLevel = hazardLevel; }
}