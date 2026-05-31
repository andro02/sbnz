package com.ftn.sbnz.model.feature;

public class City extends NearbyFeature {
    private int population;

    public City(String name, double distanceM, int population) {
        super(name, distanceM);
        this.population = population;
    }

    @Override
    public double computeRisk() {
        double sizeMultiplier = population > 100000 ? 3.0 : population > 10000 ? 2.0 : 1.0;
        return sizeMultiplier * (1.0 / (1 + distanceM / 300.0));
    }

    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }
}