package com.ftn.sbnz.service.repository;

import com.ftn.sbnz.model.entity.River;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RiverRepository extends JpaRepository<River, Integer> {

    @Query(value = "SELECT id, name, importance," + 
            "ST_Distance(geom, ST_MakePoint(:lon, :lat)::geography) as distance_m" + 
            "FROM rivers" + 
            "WHERE ST_DWithin(geom, ST_MakePoint(:lon,:lat)::geography, :radiusM)" +
            "ORDER BY distance_m", 
            nativeQuery = true)
    List<Object[]> findNearby(@Param("lat") double lat,
                               @Param("lon") double lon,
                               @Param("radiusM") double radiusM);
}