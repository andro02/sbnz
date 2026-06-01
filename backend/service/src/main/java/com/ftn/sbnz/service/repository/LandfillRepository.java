package com.ftn.sbnz.service.repository;

import com.ftn.sbnz.model.entity.Landfill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandfillRepository extends JpaRepository<Landfill, Integer> {}