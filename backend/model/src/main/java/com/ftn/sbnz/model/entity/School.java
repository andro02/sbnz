package com.ftn.sbnz.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "schools")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "type")
    private String type;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getStudentCount() { return studentCount; }
    public String getType() { return type; }
}