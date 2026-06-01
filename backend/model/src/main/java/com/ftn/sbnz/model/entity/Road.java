package com.ftn.sbnz.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "roads")
public class Road {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "road_type")
    private String roadType;

    @Column(name = "is_main_road")
    private Boolean isMainRoad;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getRoadType() { return roadType; }
    public Boolean getIsMainRoad() { return isMainRoad; }
}