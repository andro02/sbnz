package com.ftn.sbnz.model.drools;

import java.util.ArrayList;
import java.util.List;

public class LogisticsOrder {
    private String dumpsiteId;
    private int deadlineDays;
    private String mehanization;
    private String budgetCategory;

    private List<String> requiredPrerequisites = new ArrayList<>();
    
    public LogisticsOrder() {}

    public LogisticsOrder(String dumpsiteId, int deadlineDays, String mehanization, String budgetCategory) {
        this.dumpsiteId = dumpsiteId;
        this.deadlineDays = deadlineDays;
        this.mehanization = mehanization;
        this.budgetCategory = budgetCategory;
    }

    public String getDumpsiteId() { return dumpsiteId; }
    public void setDumpsiteId(String dumpsiteId) { this.dumpsiteId = dumpsiteId; }
    public int getDeadlineDays() { return deadlineDays; }
    public void setDeadlineDays(int deadlineDays) { this.deadlineDays = deadlineDays; }
    public String getMehanization() { return mehanization; }
    public void setMehanization(String mehanization) { this.mehanization = mehanization; }
    public String getBudgetCategory() { return budgetCategory; }
    public void setBudgetCategory(String budgetCategory) { this.budgetCategory = budgetCategory; }
    public List<String> getRequiredPrerequisites() { return requiredPrerequisites; }
    public void setRequiredPrerequisites(List<String> requiredPrerequisites) { this.requiredPrerequisites = requiredPrerequisites; }

    @Override
    public String toString() {
        return "LogisticsOrder{id=" + dumpsiteId + ", rok=" + deadlineDays +
               " dana, mehanizacija=" + mehanization + ", budzet=" + budgetCategory + ", potrebniPreduslovi=" + requiredPrerequisites + "}";
    }
}