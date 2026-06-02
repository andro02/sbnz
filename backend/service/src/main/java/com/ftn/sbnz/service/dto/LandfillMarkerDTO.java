package com.ftn.sbnz.service.dto;

public class LandfillMarkerDTO {
    private Integer id;
    private Double centerLat;
    private Double centerLon;
    private String status;
    
    public LandfillMarkerDTO(Integer id, Double centerLat, Double centerLon, String status) {
        this.id = id;
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public Double getCenterLat() {
        return centerLat;
    }

    public Double getCenterLon() {
        return centerLon;
    }

    public String getStatus() {
        return status;
    }
}