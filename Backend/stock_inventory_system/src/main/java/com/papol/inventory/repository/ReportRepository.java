package com.papol.inventory.repository;

// WHY: Standard JpaRepository — provides automatic CRUD for Report entities.
// WHAT: Manages Report metadata in the 'reports' table.
// NOTE: Reports are metadata records (who generated what, when). The actual
//       analytics data is computed live by ReportService, not stored here.

import com.papol.inventory.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {}