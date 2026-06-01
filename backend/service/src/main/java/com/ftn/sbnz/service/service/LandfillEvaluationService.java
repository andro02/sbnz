package com.ftn.sbnz.service.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.drools.Dumpsite;
import com.ftn.sbnz.model.drools.GeoPoint;
import com.ftn.sbnz.model.drools.feature.NearbyFeature;
import com.ftn.sbnz.model.entity.Landfill;
import com.ftn.sbnz.service.repository.LandfillRepository;

@Service
public class LandfillEvaluationService {

    private final LandfillRepository landfillRepository;
    private final DumpsiteRiskService dumpsiteRiskService;

    public LandfillEvaluationService(LandfillRepository landfillRepository,
                                      DumpsiteRiskService dumpsiteRiskService) {
        this.landfillRepository = landfillRepository;
        this.dumpsiteRiskService = dumpsiteRiskService;
    }

    public Dumpsite evaluateLandfill(Integer landfillId, List<NearbyFeature> nearbyFeatures) {
        Landfill landfill = landfillRepository.findById(landfillId).orElseThrow();

        // Random datum u poslednjih mesec dana
        long now = System.currentTimeMillis();
        long monthAgo = now - (30L * 24 * 60 * 60 * 1000);
        Date randomDate = new Date(ThreadLocalRandom.current().nextLong(monthAgo, now));

        // Random temperatura od 10 do 40
        double randomTemp = 10 + ThreadLocalRandom.current().nextDouble() * 30;

        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId(String.valueOf(landfill.getId()));
        dumpsite.setAreaSqm(landfill.getAreaM2());
        dumpsite.setLocation(new GeoPoint(landfill.getCenterLat(), landfill.getCenterLon()));
        dumpsite.setDetectedAt(randomDate);
        dumpsite.setTemperature(randomTemp);
        dumpsite.setNearbyFeatures(nearbyFeatures);

        // Pokreni Drools evaluaciju
        return dumpsiteRiskService.evaluateRisk(dumpsite);
    }
}