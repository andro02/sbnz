package com.ftn.sbnz.service.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.drools.Dumpsite;
import com.ftn.sbnz.model.drools.GeoPoint;
import com.ftn.sbnz.model.drools.feature.City;
import com.ftn.sbnz.model.drools.feature.IndustrialZone;
import com.ftn.sbnz.model.drools.feature.Lake;
import com.ftn.sbnz.model.drools.feature.NearbyFeature;
import com.ftn.sbnz.model.drools.feature.River;
import com.ftn.sbnz.model.drools.feature.Road;
import com.ftn.sbnz.model.drools.feature.School;
import com.ftn.sbnz.model.entity.Landfill;
import com.ftn.sbnz.service.repository.CityRepository;
import com.ftn.sbnz.service.repository.IndustrialZoneRepository;
import com.ftn.sbnz.service.repository.LakeRepository;
import com.ftn.sbnz.service.repository.LandfillRepository;
import com.ftn.sbnz.service.repository.RiverRepository;
import com.ftn.sbnz.service.repository.RoadRepository;
import com.ftn.sbnz.service.repository.SchoolRepository;

@Service
public class LandfillEvaluationService {

    private final LandfillRepository landfillRepository;
    private final DumpsiteRiskService dumpsiteRiskService;
    private final RiverRepository riverRepository;
    private final LakeRepository lakeRepository;
    private final CityRepository cityRepository;
    private final RoadRepository roadRepository;
    private final SchoolRepository schoolRepository;
    private final IndustrialZoneRepository industrialZoneRepository;

    public LandfillEvaluationService(
            LandfillRepository landfillRepository,
            DumpsiteRiskService dumpsiteRiskService,
            RiverRepository riverRepository,
            LakeRepository lakeRepository,
            CityRepository cityRepository,
            RoadRepository roadRepository,
            SchoolRepository schoolRepository,
            IndustrialZoneRepository industrialZoneRepository) {
        this.landfillRepository = landfillRepository;
        this.dumpsiteRiskService = dumpsiteRiskService;
        this.riverRepository = riverRepository;
        this.lakeRepository = lakeRepository;
        this.cityRepository = cityRepository;
        this.roadRepository = roadRepository;
        this.schoolRepository = schoolRepository;
        this.industrialZoneRepository = industrialZoneRepository;
    }

    public Dumpsite evaluateLandfill(Integer landfillId) {
        Landfill landfill = landfillRepository.findById(landfillId).orElseThrow();

        double lat = landfill.getCenterLat();
        double lon = landfill.getCenterLon();

        List<NearbyFeature> features = new ArrayList<>();

        // Reke
        riverRepository.findNearby(lat, lon, 5000).forEach(row -> {
            String name = (String) row[1];
            Integer importance = (Integer) row[2];
            Double distance = (Double) row[3];
            features.add(new River(name, distance, importance));
        });

        // Jezera
        lakeRepository.findNearby(lat, lon, 3000).forEach(row -> {
            String name = (String) row[1];
            Double distance = (Double) row[3];
            features.add(new Lake(name, distance));
        });

        // Gradovi
        cityRepository.findNearby(lat, lon, 3000).forEach(row -> {
            String name = (String) row[1];
            Integer population = (Integer) row[2];
            Double distance = (Double) row[3];
            features.add(new City(name, distance, population));
        });

        // Putevi
        roadRepository.findNearby(lat, lon, 1000).forEach(row -> {
            String name = (String) row[1];
            String roadType = (String) row[2];
            Boolean isMainRoad = (Boolean) row[3];
            Double distance = (Double) row[4];
            features.add(new Road(name, distance, roadType, isMainRoad));
        });

        // Škole
        schoolRepository.findNearby(lat, lon, 2000).forEach(row -> {
            String name = (String) row[1];
            Integer studentCount = (Integer) row[2];
            String type = (String) row[3];
            Double distance = (Double) row[4];
            features.add(new School(name, distance, studentCount, type));
        });

        // Industrijske zone
        industrialZoneRepository.findNearby(lat, lon, 2000).forEach(row -> {
            String name = (String) row[1];
            String zoneType = (String) row[2];
            Integer hazardLevel = (Integer) row[3];
            Double distance = (Double) row[4];
            features.add(new IndustrialZone(name, distance, zoneType, hazardLevel));
        });

        boolean hasRoads = features.stream().anyMatch(f -> f instanceof Road);
        if (!hasRoads) {
            features.add(new Road("Nepoznat put", 9999, "unknown", false));
        }

        // Konvertuj i evaluiraj
        long now = System.currentTimeMillis();
        long monthAgo = now - (30L * 24 * 60 * 60 * 1000);
        Date randomDate = new Date(ThreadLocalRandom.current().nextLong(monthAgo, now));
        double randomTemp = 10 + ThreadLocalRandom.current().nextDouble() * 30;

        Dumpsite dumpsite = new Dumpsite();
        dumpsite.setId(String.valueOf(landfill.getId()));
        dumpsite.setAreaSqm(landfill.getAreaM2());
        dumpsite.setLocation(new GeoPoint(lat, lon));
        dumpsite.setDetectedAt(randomDate);
        dumpsite.setTemperature(randomTemp);
        dumpsite.setNearbyFeatures(features);

        Dumpsite result = dumpsiteRiskService.evaluateRisk(dumpsite);

        // Ukloni dummy put iz response-a
        result.getNearbyFeatures().removeIf(f -> 
            f instanceof Road && ((Road) f).getDistanceM() == 9999.0
        );

        return result;
    }
}