package com.papol.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// WHY: Report stores metadata about generated reports (who created it, when, what type).
//      It does NOT store actual report data — the real analytics are computed live
//      by the ReportService from the products and transactions tables.
// WHAT: Maps to the 'reports' table. Each row = one report generation event.
// NOTE: This entity demonstrates ENCAPSULATION with private fields and getters/setters.
//       It has no parameterised constructor — objects are built by setting fields individually.

@Entity
@Table(name = "reports")
public class Report {

    // WHY: Private fields — ENCAPSULATION. Report metadata is protected from
    //      direct external modification.
    @Id
    @Column(name = "report_id")
    private String reportId;

    @Column(name = "report_type")
    private String reportType;

    // NOTE: generatedBy stores the user ID of whoever triggered the report.
    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "generated_date")
    private LocalDate generatedDate;

    // WHY: JPA no-arg constructor requirement.
    public Report() {}

    // WHY: Getters and setters — Encapsulation access mechanism.
    public String    getReportId()                  { return reportId; }
    public void      setReportId(String id)         { this.reportId = id; }

    public String    getReportType()                { return reportType; }
    public void      setReportType(String t)        { this.reportType = t; }

    public String    getGeneratedBy()               { return generatedBy; }
    public void      setGeneratedBy(String by)      { this.generatedBy = by; }

    public LocalDate getGeneratedDate()             { return generatedDate; }
    public void      setGeneratedDate(LocalDate d)  { this.generatedDate = d; }
}