package com.ftn.sbnz.model.drools;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("detectedAt")
public class DumpsiteDetectionEvent {
    private String dumpsiteId;
    private String region;
    private double lat;
    private double lon;
    private boolean nearRiver;
    private boolean nearCity;
    private long detectedAt;

    public DumpsiteDetectionEvent(String dumpsiteId, String region,
                                   double lat, double lon,
                                   boolean nearRiver, boolean nearCity) {
        this.dumpsiteId = dumpsiteId;
        this.region = region;
        this.lat = lat;
        this.lon = lon;
        this.nearRiver = nearRiver;
        this.nearCity = nearCity;
        this.detectedAt = System.currentTimeMillis();
    }

    public String getDumpsiteId() { return dumpsiteId; }
    public String getRegion() { return region; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public boolean isNearRiver() { return nearRiver; }
    public boolean isNearCity() { return nearCity; }
    public long getDetectedAt() { return detectedAt; }
}