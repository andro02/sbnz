package com.ftn.sbnz.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "population")
    private Integer population;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getPopulation() { return population; }
}