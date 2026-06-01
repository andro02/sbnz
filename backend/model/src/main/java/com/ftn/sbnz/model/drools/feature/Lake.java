package com.ftn.sbnz.model.drools.feature;

public class Lake extends NearbyFeature {

    public Lake(String name, double distanceM) {
        super(name, distanceM);
    }

    @Override
    public double computeRisk() {
        return 0.8 * (1.0 / (1 + distanceM / 150.0));
    }
}