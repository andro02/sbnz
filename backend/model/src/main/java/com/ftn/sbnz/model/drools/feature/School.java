package com.ftn.sbnz.model.drools.feature;

public class School extends NearbyFeature {
    private int studentCount;
    private String type;

    public School(String name, double distanceM, int studentCount, String type) {
        super(name, distanceM);
        this.studentCount = studentCount;
        this.type = type;
    }

    @Override
    public double computeRisk() {
        return 1.5 * (1.0 / (1 + distanceM / 400.0));
    }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}