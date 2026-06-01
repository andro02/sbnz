package com.ftn.sbnz.service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.drools.Dumpsite;
import com.ftn.sbnz.model.entity.Landfill;
import com.ftn.sbnz.service.repository.LandfillRepository;
import com.ftn.sbnz.service.service.LandfillEvaluationService;

@RestController
@RequestMapping("/api/landfills")
@CrossOrigin(origins = "*")
public class LandfillController {

    private final LandfillRepository landfillRepository;
    private final LandfillEvaluationService landfillEvaluationService;

    public LandfillController(LandfillRepository landfillRepository, LandfillEvaluationService landfillEvaluationService) {
        this.landfillRepository = landfillRepository;
        this.landfillEvaluationService = landfillEvaluationService;
    }

    @GetMapping
    public List<Landfill> getAll() {
        return landfillRepository.findAll();
    }

    @GetMapping("/{id}")
    public Landfill getById(@PathVariable Integer id) {
        return landfillRepository.findById(id).orElse(null);
    }

    @PostMapping("/{id}/evaluate")
    public ResponseEntity<Dumpsite> evaluate(@PathVariable Integer id) {
        Dumpsite result = landfillEvaluationService.evaluateLandfill(id);
        return ResponseEntity.ok(result);
    }
}