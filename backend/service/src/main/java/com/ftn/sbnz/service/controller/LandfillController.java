package com.ftn.sbnz.service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.entity.Landfill;
import com.ftn.sbnz.service.repository.LandfillRepository;

@RestController
@RequestMapping("/api/landfills")
@CrossOrigin(origins = "*")
public class LandfillController {

    private final LandfillRepository landfillRepository;

    public LandfillController(LandfillRepository landfillRepository) {
        this.landfillRepository = landfillRepository;
    }

    @GetMapping
    public List<Landfill> getAll() {
        return landfillRepository.findAll();
    }

    @GetMapping("/{id}")
    public Landfill getById(@PathVariable Integer id) {
        return landfillRepository.findById(id).orElse(null);
    }
}