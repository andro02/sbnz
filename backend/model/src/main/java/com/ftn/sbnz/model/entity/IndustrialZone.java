package com.ftn.sbnz.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "industrial_zones")
public class IndustrialZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "zone_type")
    private String zoneType;

    @Column(name = "hazard_level")
    private Integer hazardLevel;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getZoneType() { return zoneType; }
    public Integer getHazardLevel() { return hazardLevel; }
}