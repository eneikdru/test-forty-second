package com.eneik.generated.models.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "workload_record")
public class WorkloadRecord {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;

    @Column(name = "instructor_name", nullable = false)
    private String instructorName;

    @Column(name = "planned_hours", nullable = false)
    private Integer plannedHours;

    @Column(name = "hourly_rate", nullable = false, precision = 19, scale = 4)
    private BigDecimal hourlyRate;

    public WorkloadRecord() {}

    public WorkloadRecord(String id, AcademicTerm term, String instructorName, Integer plannedHours, BigDecimal hourlyRate) {
        this.id = id;
        this.term = term;
        this.instructorName = instructorName;
        this.plannedHours = plannedHours;
        this.hourlyRate = hourlyRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AcademicTerm getTerm() {
        return term;
    }

    public void setTerm(AcademicTerm term) {
        this.term = term;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public Integer getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(Integer plannedHours) {
        this.plannedHours = plannedHours;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
