package com.eneik.generated.models.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "student_performance")
public class StudentPerformance {

    @Id
    @Column(name = "student_id")
    private String studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal gpa;

    public StudentPerformance() {}

    public StudentPerformance(String studentId, String studentName, BigDecimal gpa) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gpa = gpa;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }
}
