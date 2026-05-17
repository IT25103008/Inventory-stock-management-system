package com.papol.inventory.repository;


import com.papol.inventory.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {}