package com.ftn.sbnz.model.drools;

import java.util.List;

public class PrerequisiteResult {
    private List<String> allPrerequisites;
    private List<String> missingPrerequisites;

    public PrerequisiteResult(List<String> allPrerequisites, List<String> missingPrerequisites) {
        this.allPrerequisites = allPrerequisites;
        this.missingPrerequisites = missingPrerequisites;
    }

    public List<String> getAllPrerequisites() { return allPrerequisites; }
    public List<String> getMissingPrerequisites() { return missingPrerequisites; }
    public boolean isReadyForSanation() { return missingPrerequisites.isEmpty(); }

    @Override
    public String toString() {
        return "Svi: " + allPrerequisites + ", Nedostaju: " + missingPrerequisites;
    }
}