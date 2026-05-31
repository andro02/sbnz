package com.ftn.sbnz.model;

import org.kie.api.definition.type.Position;

public class SanitationPrerequisite {

    @Position(0)
    private String resource;

    @Position(1)
    private String goal;

    public SanitationPrerequisite(String resource, String goal) {
        this.resource = resource;
        this.goal = goal;
    }

    public String getResource() { return resource; }
    public String getGoal() { return goal; }
}