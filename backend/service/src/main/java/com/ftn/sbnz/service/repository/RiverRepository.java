package com.ftn.sbnz.service.repository;

import com.ftn.sbnz.model.entity.River;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RiverRepository extends JpaRepository<River, Integer> {

    @Query(value =
        "SELECT id, name, importance, " +
        "ST_Distance(geom, ST_MakePoint(?2, ?1)) AS distance_m " +
        "FROM rivers " +
        "WHERE ST_DWithin(geom, ST_MakePoint(?2, ?1), ?3) " +
        "ORDER BY distance_m",
        nativeQuery = true)
    List<Object[]> findNearby(double lat, double lon, double radiusM);
}