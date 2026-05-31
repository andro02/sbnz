package com.ftn.sbnz.model;

public class MissingPrerequisite {
    private String dumpsiteId;
    private String prerequisiteName;

    public MissingPrerequisite(String dumpsiteId, String prerequisiteName) {
        this.dumpsiteId = dumpsiteId;
        this.prerequisiteName = prerequisiteName;
    }

    public String getDumpsiteId() { return dumpsiteId; }
    public String getPrerequisiteName() { return prerequisiteName; }

    @Override
    public String toString() {
        return "MissingPrerequisite{deponija=" + dumpsiteId + ", nedostaje=" + prerequisiteName + "}";
    }
}