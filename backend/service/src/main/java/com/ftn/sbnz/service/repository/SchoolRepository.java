package com.ftn.sbnz.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ftn.sbnz.model.entity.School;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {

    @Query(value = "SELECT id, name, student_count, type, " +
            "ST_Distance(geom, ST_MakePoint(?2, ?1)) as distance_m " +
            "FROM schools " +
            "WHERE ST_DWithin(geom, ST_MakePoint(?2, ?1), ?3) " +
            "ORDER BY distance_m",
            nativeQuery = true)
    List<Object[]> findNearby(double lat, double lon, double radiusM);
}