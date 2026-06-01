package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.model.drools.Dumpsite;
import com.ftn.sbnz.model.drools.DumpsiteDetectionEvent;
import com.ftn.sbnz.model.drools.PrerequisiteResult;
import com.ftn.sbnz.service.service.DumpsiteRiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dumpsites")
@CrossOrigin(origins = "*")
public class DumpsiteController {

    private final DumpsiteRiskService dumpsiteRiskService;
    private final List<Dumpsite> dumpsiteStorage = new ArrayList<>();

    public DumpsiteController(DumpsiteRiskService dumpsiteRiskService) {
        this.dumpsiteRiskService = dumpsiteRiskService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Dumpsite> evaluate(@RequestBody Dumpsite dumpsite) {
        Dumpsite result = dumpsiteRiskService.evaluateRisk(dumpsite);
        dumpsiteStorage.add(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<Dumpsite>> getAll() {
        return ResponseEntity.ok(dumpsiteStorage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dumpsite> getById(@PathVariable String id) {
        return dumpsiteStorage.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/prerequisites")
    public ResponseEntity<List<String>> getPrerequisites(@PathVariable String id) {
        Optional<Dumpsite> dumpsite = dumpsiteStorage.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();

        if (dumpsite.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PrerequisiteResult prerequisiteResult = dumpsiteRiskService.checkPrerequisites(dumpsite.get(), new ArrayList<>());
        return ResponseEntity.ok(prerequisiteResult.getMissingPrerequisites());
    }

    @PostMapping("/detect")
    public ResponseEntity<Void> detect(@RequestBody DumpsiteDetectionEvent event) {
        dumpsiteRiskService.processDetectionEvent(event);
        return ResponseEntity.ok().build();
    }
}