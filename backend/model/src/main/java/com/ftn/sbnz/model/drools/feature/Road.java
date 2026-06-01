package com.ftn.sbnz.model.drools.feature;

public class Road extends NearbyFeature {
    private String roadType;
    private boolean isMainRoad;

    public Road(String name, double distanceM, String roadType, boolean isMainRoad) {
        super(name, distanceM);
        this.roadType = roadType;
        this.isMainRoad = isMainRoad;
    }

    @Override
    public double computeRisk() {
        return 1.0 / (1 + distanceM / 100.0);
    }

    public String getRoadType() { return roadType; }
    public void setRoadType(String roadType) { this.roadType = roadType; }
    public boolean isMainRoad() { return isMainRoad; }
    public void setMainRoad(boolean mainRoad) { isMainRoad = mainRoad; }
}