package com.eneik.generated.dtos;

import java.math.BigDecimal;

public class WorkloadRecordDto {
    private String id;
    private String termId;
    private String instructorName;
    private Integer plannedHours;
    private BigDecimal hourlyRate;

    public WorkloadRecordDto() {}

    public WorkloadRecordDto(String id, String termId, String instructorName, Integer plannedHours, BigDecimal hourlyRate) {
        this.id = id;
        this.termId = termId;
        this.instructorName = instructorName;
        this.plannedHours = plannedHours;
        this.hourlyRate = hourlyRate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTermId() { return termId; }
    public void setTermId(String termId) { this.termId = termId; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public Integer getPlannedHours() { return plannedHours; }
    public void setPlannedHours(Integer plannedHours) { this.plannedHours = plannedHours; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
}
