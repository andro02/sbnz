package com.ftn.sbnz.model.feature;

public class River extends NearbyFeature {
    private int importance; // 1-10

    public River(String name, double distanceM, int importance) {
        super(name, distanceM);
        this.importance = importance;
    }

    @Override
    public double computeRisk() {
        return importance * (1.0 / (1 + distanceM / 200.0));
    }

    public int getImportance() { return importance; }
    public void setImportance(int importance) { this.importance = importance; }
}