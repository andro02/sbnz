package com.ftn.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ftn.sbnz.model.drools.Dumpsite;
import com.ftn.sbnz.model.drools.DumpsiteDetectionEvent;
import com.ftn.sbnz.model.drools.GeoPoint;
import com.ftn.sbnz.model.drools.LogisticsOrder;
import com.ftn.sbnz.model.drools.Notification;
import com.ftn.sbnz.model.drools.PrerequisiteResult;
import com.ftn.sbnz.model.drools.RiskLevel;
import com.ftn.sbnz.model.drools.feature.City;
import com.ftn.sbnz.model.drools.feature.IndustrialZone;
import com.ftn.sbnz.model.drools.feature.Lake;
import com.ftn.sbnz.model.drools.feature.River;
import com.ftn.sbnz.model.drools.feature.Road;
import com.ftn.sbnz.model.drools.feature.School;
import com.ftn.sbnz.service.service.DumpsiteRiskService;

@SpringBootTest
class ServiceApplicationTests {

	@Autowired
    private DumpsiteRiskService dumpsiteRiskService;

    @Test
    public void testCriticalRisk() {
        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId("DEP-001");
        dumpsite.setAreaSqm(5000000);
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
        System.out.println("Access possible: " + result.isAccessPossible());

        if (result.getLogisticsOrder() != null) {
            System.out.println("=== LOGISTICKI NALOG ===");
            System.out.println("Mehanizacija: " + result.getLogisticsOrder().getMehanization());
            System.out.println("Rok: " + result.getLogisticsOrder().getDeadlineDays() + " dana");
            System.out.println("Budzet: " + result.getLogisticsOrder().getBudgetCategory());
        }

        System.out.println("=== PREDUSLOVI ===");
        result.getPrerequisiteResult().getAllPrerequisites()
            .forEach(p -> System.out.println("  - " + p));
        System.out.println("Nedostaju: " + result.getPrerequisiteResult().getMissingPrerequisites());
        System.out.println("Spreman za sanaciju: " + result.getPrerequisiteResult().isReadyForSanation());

        assertNotNull(result.getRiskAssessment().getRiskLevel());
        assertNotNull(result.getLogisticsOrder());
        assertFalse(result.getPrerequisiteResult().getAllPrerequisites().isEmpty());
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

        System.out.println("=== REZULTAT (umeren) ===");
        System.out.println("Total risk: " + result.getRiskAssessment().getTotalRisk());
        System.out.println("Risk level: " + result.getRiskAssessment().getRiskLevel());

        if (result.getLogisticsOrder() != null) {
            System.out.println("Mehanizacija: " + result.getLogisticsOrder().getMehanization());
            System.out.println("Rok: " + result.getLogisticsOrder().getDeadlineDays() + " dana");
        }

        assertEquals(RiskLevel.MODERATE, result.getRiskAssessment().getRiskLevel());
        assertNotNull(result.getLogisticsOrder());
        // MODERATE bez pristupa -> Traktor, 45 dana
        assertEquals("Traktor", result.getLogisticsOrder().getMehanization());
        assertEquals(45, result.getLogisticsOrder().getDeadlineDays());
    }

    @Test
    public void testBackwardChaining() {
        // Kreiraj deponiju sa već izračunatim rizikom (simuliramo da je forward već odrađen)
        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId("DEP-003");
        dumpsite.setAreaSqm(10000);
        dumpsite.setTemperature(25);
        dumpsite.setDetectedAt(new Date());
        dumpsite.setLocation(new GeoPoint(45.25, 19.83));
        dumpsite.setNearbyFeatures(new ArrayList<>());

        // Postavi riskLevel direktno da ne moramo pokretati forward
        dumpsite.getRiskAssessment().setRiskLevel(RiskLevel.HIGH);
        dumpsite.getRiskAssessment().setTotalRisk(55.0);

        LogisticsOrder mockOrder = new LogisticsOrder();
        mockOrder.setDumpsiteId("DEP-003");
        mockOrder.setDeadlineDays(7);
        mockOrder.setMehanization("Bager + 1 kamion");
        mockOrder.setBudgetCategory("Redovni");
        mockOrder.setRequiredPrerequisites(List.of("BagerDostupan", "RedovniBudzet", "DozvolaOpstine"));
        dumpsite.setLogisticsOrder(mockOrder);

        PrerequisiteResult result = dumpsiteRiskService.checkPrerequisites(dumpsite, new ArrayList<>());

        System.out.println("=== PREDUSLOVI ZA SANACIJU ===");
        result.getAllPrerequisites().forEach(p -> System.out.println("  - " + p));
        System.out.println("Nedostaju: " + result.getMissingPrerequisites());

        assertFalse(result.getAllPrerequisites().isEmpty());
        assertTrue(result.getAllPrerequisites().contains("BagerDostupan"));
        assertTrue(result.getAllPrerequisites().contains("RedovniBudzet"));
        assertTrue(result.getAllPrerequisites().contains("DozvolaOpstine"));
        assertFalse(result.getMissingPrerequisites().isEmpty()); // nista nije fulfilled
    }

    @Test
    public void testBackwardChainingAllFulfilled() {
        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId("DEP-005");
        dumpsite.setAreaSqm(10000);
        dumpsite.setTemperature(25);
        dumpsite.setDetectedAt(new Date());
        dumpsite.setLocation(new GeoPoint(45.25, 19.83));
        dumpsite.setNearbyFeatures(new ArrayList<>());

        // Simuliramo forward - umjeren rizik sa pristupom
        dumpsite.getRiskAssessment().setRiskLevel(RiskLevel.MODERATE);
        dumpsite.getRiskAssessment().setTotalRisk(30.0);
        dumpsite.setAccessPossible(true);

        LogisticsOrder mockOrder = new LogisticsOrder();
        mockOrder.setDumpsiteId("DEP-005");
        mockOrder.setDeadlineDays(30);
        mockOrder.setMehanization("Standardno komunalno vozilo");
        mockOrder.setBudgetCategory("Minimalni");
        mockOrder.setRequiredPrerequisites(List.of("KomunalnoVoziloDostupno", "MinimalniBudzet", "DozvolaOpstine"));
        dumpsite.setLogisticsOrder(mockOrder);

        // Svi preduslovi ispunjeni - moraju odgovarati requiredPrerequisites
        List<String> fulfilled = List.of(
            "KomunalnoVoziloDostupno",
            "MinimalniBudzet",
            "DozvolaOpstine"
        );

        PrerequisiteResult result = dumpsiteRiskService.checkPrerequisites(dumpsite, fulfilled);

        assertFalse(result.getAllPrerequisites().isEmpty());
        assertTrue(result.getMissingPrerequisites().isEmpty());
        assertTrue(result.isReadyForSanation());
    }

    @Test
    public void testTemplate() {
        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId("DEP-004");
        dumpsite.setAreaSqm(50000);
        dumpsite.setTemperature(35);
        dumpsite.setDetectedAt(new Date());
        dumpsite.setLocation(new GeoPoint(45.25, 19.83));
        dumpsite.setNearbyFeatures(new ArrayList<>());

        dumpsite.getRiskAssessment().setRiskLevel(RiskLevel.HIGH);
        dumpsite.getRiskAssessment().setTotalRisk(55.0);
        dumpsite.setAccessPossible(true);

        // 1. Template generiše nalog
        LogisticsOrder order = dumpsiteRiskService.generateLogisticsOrder(dumpsite);
        dumpsite.setLogisticsOrder(order);

        System.out.println("=== LOGISTICKI NALOG ===");
        System.out.println("Deponija: " + order.getDumpsiteId());
        System.out.println("Mehanizacija: " + order.getMehanization());
        System.out.println("Rok: " + order.getDeadlineDays() + " dana");
        System.out.println("Budzet: " + order.getBudgetCategory());

        assertNotNull(order);
        assertEquals("DEP-004", order.getDumpsiteId());
        assertEquals(7, order.getDeadlineDays());
        assertEquals("Bager + 1 kamion", order.getMehanization());
        assertEquals("Redovni", order.getBudgetCategory());

        // 2. Backward provjerava preduslove za taj nalog
        PrerequisiteResult prereqResult = dumpsiteRiskService.checkPrerequisites(dumpsite, new ArrayList<>());

        System.out.println("=== PREDUSLOVI ZA NALOG ===");
        prereqResult.getAllPrerequisites().forEach(p -> System.out.println("  - " + p));
        System.out.println("Spreman za sanaciju: " + prereqResult.isReadyForSanation());

        assertFalse(prereqResult.getAllPrerequisites().isEmpty());
    }

    @Test
    public void testCEPCluster() throws InterruptedException {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession session = kc.newKieSession("cepKsessionPseudo");
        SessionPseudoClock clock = session.getSessionClock();

        // Prva deponija
        DumpsiteDetectionEvent e1 = new DumpsiteDetectionEvent(
            "DEP-C01", "Vojvodina", 45.25, 19.83, true, false);
        session.insert(e1);
        session.fireAllRules();

        // Pomjeri sat za 1 sat
        clock.advanceTime(1, TimeUnit.HOURS);

        // Druga deponija
        DumpsiteDetectionEvent e2 = new DumpsiteDetectionEvent(
            "DEP-C02", "Vojvodina", 45.26, 19.84, false, true);
        session.insert(e2);
        session.fireAllRules();

        // Pomjeri sat za još 1 sat
        clock.advanceTime(1, TimeUnit.HOURS);

        // Treća deponija - treba okidati klaster jer su sve unutar 24h
        DumpsiteDetectionEvent e3 = new DumpsiteDetectionEvent(
            "DEP-C03", "Vojvodina", 45.27, 19.85, false, false);
        session.insert(e3);
        session.fireAllRules();

        long notifications = session.getObjects(obj -> obj instanceof Notification)
            .stream().count();

        System.out.println("Notifikacija: " + notifications);
        assertTrue(notifications > 0);

        session.dispose();
    }
}
