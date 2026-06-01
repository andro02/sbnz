package com.ftn.sbnz.service.repository;

import com.ftn.sbnz.model.entity.Lake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LakeRepository extends JpaRepository<Lake, Integer> {

    @Query(value = "SELECT id, name, surface_area_ha, " +
            "ST_Distance(geom, ST_MakePoint(:lon, :lat)::geography) as distance_m " +
            "FROM lakes " +
            "WHERE ST_DWithin(geom, ST_MakePoint(:lon, :lat)::geography, :radiusM) " +
            "ORDER BY distance_m",
            nativeQuery = true)
    List<Object[]> findNearby(@Param("lat") double lat,
                               @Param("lon") double lon,
                               @Param("radiusM") double radiusM);
}