package com.ftn.sbnz.service.repository;

import com.ftn.sbnz.model.entity.Landfill;
import com.ftn.sbnz.service.dto.LandfillMarkerDTO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LandfillRepository extends JpaRepository<Landfill, Integer> {

    @Query("SELECT new com.ftn.sbnz.service.dto.LandfillMarkerDTO(l.id, l.centerLat, l.centerLon, l.status) FROM Landfill l")
    List<LandfillMarkerDTO> findAllMarkers();
    
}