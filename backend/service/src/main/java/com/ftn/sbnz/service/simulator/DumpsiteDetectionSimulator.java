package com.ftn.sbnz.service.simulator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ftn.sbnz.model.drools.DumpsiteDetectionEvent;
import com.ftn.sbnz.model.entity.Landfill;
import com.ftn.sbnz.service.repository.LandfillRepository;
import com.ftn.sbnz.service.service.DumpsiteRiskService;

@Component
public class DumpsiteDetectionSimulator {

    private final DumpsiteRiskService dumpsiteRiskService;
    private final LandfillRepository landfillRepository;

     public DumpsiteDetectionSimulator(DumpsiteRiskService dumpsiteRiskService, LandfillRepository landfillRepository) {
        this.dumpsiteRiskService = dumpsiteRiskService;
        this.landfillRepository = landfillRepository;
    }

    @Scheduled(fixedRate = 30000) // svakih 30 sekundi
    public void simulateDetection() {
        // Uzmi random deponiju iz baze i pošalji kao CEP event
        List<Landfill> landfills = landfillRepository.findAll();
        if (landfills.isEmpty()) return;

        Landfill random = landfills.get(
            ThreadLocalRandom.current().nextInt(landfills.size()));

        DumpsiteDetectionEvent event = new DumpsiteDetectionEvent(
            String.valueOf(random.getId()),
            "Vojvodina",
            random.getCenterLat(),
            random.getCenterLon(),
            true,
            true
        );

        dumpsiteRiskService.processDetectionEvent(event);
    }
}
