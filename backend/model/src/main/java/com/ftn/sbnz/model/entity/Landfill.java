package com.ftn.sbnz.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "landfills")
public class Landfill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "status")
    private String status;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "life_years")
    private Integer lifeYears;

    @Column(name = "area_m2")
    private Double areaM2;

    @Column(name = "volume_m3")
    private Double volumeM3;

    @Column(name = "total_mass_ton")
    private Double totalMassTon;

    @Column(name = "annual_msw_m3")
    private Double annualMswM3;

    @Column(name = "annual_ch4_tonnes")
    private Double annualCh4Tonnes;

    @Column(name = "annual_co2e_tonnes")
    private Double annualCo2eTonnes;

    @Column(name = "geojson", columnDefinition = "jsonb")
    private String geojson;

    @Column(name = "segmentation", columnDefinition = "jsonb")
    private String segmentation;

    @Column(name = "center_lat")
    private Double centerLat;

    @Column(name = "center_lon")
    private Double centerLon;

    @Column(name = "influence_radius")
    private Double influenceRadius;

    @Column(name = "center_x_px")
    private Double centerXPx;

    @Column(name = "center_y_px")
    private Double centerYPx;

    @Column(name = "width_px")
    private Double widthPx;

    @Column(name = "height_px")
    private Double heightPx;

    // getteri/setteri
    public Integer getId() { return id; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }
    public Integer getLifeYears() { return lifeYears; }
    public void setLifeYears(Integer lifeYears) { this.lifeYears = lifeYears; }
    public Double getAreaM2() { return areaM2; }
    public void setAreaM2(Double areaM2) { this.areaM2 = areaM2; }
    public Double getVolumeM3() { return volumeM3; }
    public void setVolumeM3(Double volumeM3) { this.volumeM3 = volumeM3; }
    public Double getTotalMassTon() { return totalMassTon; }
    public void setTotalMassTon(Double totalMassTon) { this.totalMassTon = totalMassTon; }
    public Double getAnnualMswM3() { return annualMswM3; }
    public void setAnnualMswM3(Double annualMswM3) { this.annualMswM3 = annualMswM3; }
    public Double getAnnualCh4Tonnes() { return annualCh4Tonnes; }
    public void setAnnualCh4Tonnes(Double annualCh4Tonnes) { this.annualCh4Tonnes = annualCh4Tonnes; }
    public Double getAnnualCo2eTonnes() { return annualCo2eTonnes; }
    public void setAnnualCo2eTonnes(Double annualCo2eTonnes) { this.annualCo2eTonnes = annualCo2eTonnes; }
    public String getGeojson() { return geojson; }
    public void setGeojson(String geojson) { this.geojson = geojson; }
    public String getSegmentation() { return segmentation; }
    public void setSegmentation(String segmentation) { this.segmentation = segmentation; }
    public Double getCenterLat() { return centerLat; }
    public void setCenterLat(Double centerLat) { this.centerLat = centerLat; }
    public Double getCenterLon() { return centerLon; }
    public void setCenterLon(Double centerLon) { this.centerLon = centerLon; }
    public Double getInfluenceRadius() { return influenceRadius; }
    public void setInfluenceRadius(Double influenceRadius) { this.influenceRadius = influenceRadius; }
    public Double getCenterXPx() { return centerXPx; }
    public void setCenterXPx(Double centerXPx) { this.centerXPx = centerXPx; }
    public Double getCenterYPx() { return centerYPx; }
    public void setCenterYPx(Double centerYPx) { this.centerYPx = centerYPx; }
    public Double getWidthPx() { return widthPx; }
    public void setWidthPx(Double widthPx) { this.widthPx = widthPx; }
    public Double getHeightPx() { return heightPx; }
    public void setHeightPx(Double heightPx) { this.heightPx = heightPx; }
}