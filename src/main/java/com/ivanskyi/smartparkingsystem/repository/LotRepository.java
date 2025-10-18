package com.ivanskyi.smartparkingsystem.repository;

import com.ivanskyi.smartparkingsystem.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
}
