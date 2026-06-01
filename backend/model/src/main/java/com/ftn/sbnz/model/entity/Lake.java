package com.ftn.sbnz.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "lakes")
public class Lake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "surface_area_ha")
    private Double surfaceAreaHa;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Double getSurfaceAreaHa() { return surfaceAreaHa; }
}