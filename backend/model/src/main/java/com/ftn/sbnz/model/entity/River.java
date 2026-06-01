package com.ftn.sbnz.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "rivers")
public class River {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "importance")
    private Integer importance;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getImportance() { return importance; }
}