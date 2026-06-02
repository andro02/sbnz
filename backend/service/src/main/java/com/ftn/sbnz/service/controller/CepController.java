package com.ftn.sbnz.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.drools.DumpsiteDetectionEvent;
import com.ftn.sbnz.service.service.DumpsiteRiskService;

@RestController
@RequestMapping("/api/cep")
public class CepController {

    private final DumpsiteRiskService dumpsiteRiskService;

    public CepController(DumpsiteRiskService dumpsiteRiskService) {
        this.dumpsiteRiskService = dumpsiteRiskService;
    }

    @PostMapping("/detect")
    public ResponseEntity<Void> detect(@RequestBody DumpsiteDetectionEvent event) {
        dumpsiteRiskService.processDetectionEvent(event);
        return ResponseEntity.ok().build();
    }
}
