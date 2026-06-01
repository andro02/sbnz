package com.ftn.sbnz.model.drools.feature;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "featureType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = River.class, name = "river"),
    @JsonSubTypes.Type(value = Lake.class, name = "lake"),
    @JsonSubTypes.Type(value = City.class, name = "city"),
    @JsonSubTypes.Type(value = Road.class, name = "road"),
    @JsonSubTypes.Type(value = School.class, name = "school"),
    @JsonSubTypes.Type(value = IndustrialZone.class, name = "industrialZone")
})
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