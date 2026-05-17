package com.papol.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "reports")
public class Report {

    @Id
    @Column(name = "report_id")
    private String reportId;

    @Column(name = "report_type")
    private String reportType;


    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "generated_date")
    private LocalDate generatedDate;


    public Report() {}

    public String    getReportId()                  { return reportId; }
    public void      setReportId(String id)         { this.reportId = id; }

    public String    getReportType()                { return reportType; }
    public void      setReportType(String t)        { this.reportType = t; }

    public String    getGeneratedBy()               { return generatedBy; }
    public void      setGeneratedBy(String by)      { this.generatedBy = by; }

    public LocalDate getGeneratedDate()             { return generatedDate; }
    public void      setGeneratedDate(LocalDate d)  { this.generatedDate = d; }
}