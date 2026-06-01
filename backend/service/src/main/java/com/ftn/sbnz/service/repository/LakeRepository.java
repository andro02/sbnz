package com.ftn.sbnz.service.repository;

import com.ftn.sbnz.model.entity.Lake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LakeRepository extends JpaRepository<Lake, Integer> {

    @Query(value = "SELECT id, name, surface_area_ha, " +
            "ST_Distance(geom, ST_MakePoint(?2, ?1)) as distance_m " +
            "FROM lakes " +
            "WHERE ST_DWithin(geom, ST_MakePoint(?2, ?1), ?3) " +
            "ORDER BY distance_m",
            nativeQuery = true)
    List<Object[]> findNearby(double lat, double lon, double radiusM);
}