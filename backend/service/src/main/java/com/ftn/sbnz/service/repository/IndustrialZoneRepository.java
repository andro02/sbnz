package com.ftn.sbnz.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ftn.sbnz.model.entity.IndustrialZone;

@Repository
public interface IndustrialZoneRepository extends JpaRepository<IndustrialZone, Integer> {

    @Query(value = "SELECT id, name, zone_type, hazard_level, " +
            "ST_Distance(geom, ST_MakePoint(:lon, :lat)::geography) as distance_m " +
            "FROM industrial_zones " +
            "WHERE ST_DWithin(geom, ST_MakePoint(:lon, :lat)::geography, :radiusM) " +
            "ORDER BY distance_m",
            nativeQuery = true)
    List<Object[]> findNearby(@Param("lat") double lat,
                               @Param("lon") double lon,
                               @Param("radiusM") double radiusM);
}