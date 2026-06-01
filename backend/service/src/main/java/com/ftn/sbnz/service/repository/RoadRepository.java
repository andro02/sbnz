package com.ftn.sbnz.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ftn.sbnz.model.entity.Road;

@Repository
public interface RoadRepository extends JpaRepository<Road, Integer> {

    @Query(value = "SELECT id, name, road_type, is_main_road, " +
            "ST_Distance(geom, ST_MakePoint(?2, ?1)) as distance_m " +
            "FROM roads " +
            "WHERE ST_DWithin(geom, ST_MakePoint(?2, ?1), ?3) " +
            "AND is_main_road = true " +
            "ORDER BY distance_m",
            nativeQuery = true)
    List<Object[]> findNearby(double lat, double lon, double radiusM);
}