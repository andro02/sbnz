package com.ftn.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ftn.sbnz.model.Dumpsite;
import com.ftn.sbnz.model.GeoPoint;
import com.ftn.sbnz.model.RiskLevel;
import com.ftn.sbnz.model.feature.City;
import com.ftn.sbnz.model.feature.IndustrialZone;
import com.ftn.sbnz.model.feature.Lake;
import com.ftn.sbnz.model.feature.River;
import com.ftn.sbnz.model.feature.Road;
import com.ftn.sbnz.model.feature.School;
import com.ftn.sbnz.service.service.DumpsiteRiskService;

@SpringBootTest
class ServiceApplicationTests {

	@Autowired
    private DumpsiteRiskService dumpsiteRiskService;

    @Test
    public void testCriticalRisk() {
        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId("DEP-001");
        dumpsite.setAreaSqm(50000);
        dumpsite.setTemperature(35);
        dumpsite.setDetectedAt(new Date());
        dumpsite.setLocation(new GeoPoint(44.0, 20.0));

        dumpsite.setNearbyFeatures(List.of(
            new River("Sava", 150, 8),
            new Lake("Palicko jezero", 200),
            new City("Novi Sad", 500, 280000),
            new City("Sremska Kamenica", 1200, 15000),
            new Road("Auto-put E75", 300, "highway", true),
            new School("OS Jovan Jovanovic Zmaj", 600, 450, "osnovna"),
            new IndustrialZone("Industrijska zona Sever", 800, "heavy", 8)
        ));

        Dumpsite result = dumpsiteRiskService.evaluateRisk(dumpsite);

        System.out.println("=== REZULTAT ===");
        System.out.println("Total risk: " + result.getRiskAssessment().getTotalRisk());
        System.out.println("Risk level: " + result.getRiskAssessment().getRiskLevel());
        System.out.println("Water risk flag: " + result.isWaterRiskFlag());
        System.out.println("Access possible: " + result.isAccessPossible());
        System.out.println("Fire risk: " + result.isFireRisk());
        System.out.println("Threatened water bodies: " + result.getThreatenedWaterBodies());
        System.out.println("Affected populations: " + result.getAffectedPopulations());

        assertNotNull(result.getRiskAssessment().getRiskLevel());
        assertTrue(result.getRiskAssessment().getTotalRisk() > 0);
    }

    @Test
    public void testModerateRisk() {
        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId("DEP-002");
        dumpsite.setAreaSqm(500);
        dumpsite.setTemperature(20);
        dumpsite.setDetectedAt(new Date());
        dumpsite.setLocation(new GeoPoint(44.5, 20.5));

        dumpsite.setNearbyFeatures(List.of(
            new Road("Lokalni put", 800, "local", false)
        ));

        Dumpsite result = dumpsiteRiskService.evaluateRisk(dumpsite);

        System.out.println("=== REZULTAT (umjeren) ===");
        System.out.println("Total risk: " + result.getRiskAssessment().getTotalRisk());
        System.out.println("Risk level: " + result.getRiskAssessment().getRiskLevel());

        assertEquals(RiskLevel.MODERATE, result.getRiskAssessment().getRiskLevel());
    }
}
