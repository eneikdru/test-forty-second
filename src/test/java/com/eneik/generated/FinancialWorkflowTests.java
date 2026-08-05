package com.eneik.generated;

import com.eneik.generated.models.persistence.AcademicTerm;
import com.eneik.generated.models.persistence.BudgetRecord;
import com.eneik.generated.models.persistence.ScholarshipRecord;
import com.eneik.generated.models.persistence.WorkloadRecord;
import com.eneik.generated.repositories.AcademicTermRepository;
import com.eneik.generated.repositories.BudgetRecordRepository;
import com.eneik.generated.repositories.ScholarshipRecordRepository;
import com.eneik.generated.repositories.WorkloadRecordRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FinancialWorkflowTests {

    @Autowired
    private AcademicTermRepository termRepository;

    @Autowired
    private BudgetRecordRepository budgetRepository;

    @Autowired
    private WorkloadRecordRepository workloadRepository;

    @Autowired
    private ScholarshipRecordRepository scholarshipRepository;

    @Autowired
    private EntityManager entityManager;

    private AcademicTerm term;

    @BeforeEach
    public void setUp() {
        scholarshipRepository.deleteAll();
        workloadRepository.deleteAll();
        budgetRepository.deleteAll();
        termRepository.deleteAll();

        // Initialize term with valid dates
        term = new AcademicTerm(
                UUID.randomUUID().toString(),
                "Fall 2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 31)
        );
        termRepository.saveAndFlush(term);
    }

    @Test
    public void testRelationalIntegrityAndConstraints() {
        // 1. Term constraint: start_date must be < end_date
        AcademicTerm invalidTerm = new AcademicTerm(
                UUID.randomUUID().toString(),
                "Invalid Term",
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 9, 1)
        );
        assertThrows(DataIntegrityViolationException.class, () -> {
            termRepository.saveAndFlush(invalidTerm);
        });

        // 2. Budget Record - category and amounts
        BudgetRecord validBudget = new BudgetRecord(
                UUID.randomUUID().toString(),
                term,
                "Scholarships",
                new BigDecimal("100000.0000"),
                new BigDecimal("50000.0000")
        );
        BudgetRecord savedBudget = budgetRepository.saveAndFlush(validBudget);
        assertNotNull(savedBudget);

        // Negative budget amount constraint check
        BudgetRecord negativeBudget = new BudgetRecord(
                UUID.randomUUID().toString(),
                term,
                "Salaries",
                new BigDecimal("-1000.0000"),
                BigDecimal.ZERO
        );
        assertThrows(DataIntegrityViolationException.class, () -> {
            budgetRepository.saveAndFlush(negativeBudget);
        });

        // Budget record: allocated_amount <= amount check
        BudgetRecord overallocatedBudget = new BudgetRecord(
                UUID.randomUUID().toString(),
                term,
                "Research",
                new BigDecimal("10000.0000"),
                new BigDecimal("12000.0000")
        );
        assertThrows(DataIntegrityViolationException.class, () -> {
            budgetRepository.saveAndFlush(overallocatedBudget);
        });

        // 3. Workload Record - hours & rates
        WorkloadRecord validWorkload = new WorkloadRecord(
                UUID.randomUUID().toString(),
                term,
                "Dr. Ivanov",
                120,
                new BigDecimal("150.0000")
        );
        WorkloadRecord savedWorkload = workloadRepository.saveAndFlush(validWorkload);
        assertNotNull(savedWorkload);

        // Negative workload hours constraint check
        WorkloadRecord negativeHoursWorkload = new WorkloadRecord(
                UUID.randomUUID().toString(),
                term,
                "Dr. Petrov",
                -10,
                new BigDecimal("100.0000")
        );
        assertThrows(DataIntegrityViolationException.class, () -> {
            workloadRepository.saveAndFlush(negativeHoursWorkload);
        });

        // Negative hourly rate constraint check
        WorkloadRecord negativeRateWorkload = new WorkloadRecord(
                UUID.randomUUID().toString(),
                term,
                "Dr. Sidorov",
                100,
                new BigDecimal("-50.0000")
        );
        assertThrows(DataIntegrityViolationException.class, () -> {
            workloadRepository.saveAndFlush(negativeRateWorkload);
        });
    }

    @Test
    public void testScholarshipImmutabilityAndTraceability() {
        LocalDateTime calculatedTime = LocalDateTime.of(2026, 8, 5, 14, 0, 0);
        ScholarshipRecord scholarship = new ScholarshipRecord(
                UUID.randomUUID().toString(),
                "ST-001",
                "Ivanov Ivan",
                term,
                new BigDecimal("15000.0000"),
                calculatedTime,
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        );

        ScholarshipRecord savedScholarship = scholarshipRepository.saveAndFlush(scholarship);
        assertNotNull(savedScholarship);

        // Clear persistence context to trigger @PostLoad on next load
        entityManager.clear();

        ScholarshipRecord retrievedScholarship = scholarshipRepository.findById(savedScholarship.getId()).orElse(null);
        assertNotNull(retrievedScholarship);

        // Traceability check
        assertEquals("ST-001", retrievedScholarship.getStudentId());
        assertEquals("Ivanov Ivan", retrievedScholarship.getStudentName());
        assertEquals(calculatedTime, retrievedScholarship.getCalculatedAt());
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", retrievedScholarship.getHashChecksum());

        // Immutability Check - trying to update student_name or amount should throw exception in @PreUpdate
        retrievedScholarship.setStudentName("Smirnov Ivan");

        Exception ex = assertThrows(Exception.class, () -> {
            scholarshipRepository.saveAndFlush(retrievedScholarship);
        });

        // Handle both direct IllegalStateException and transaction wrapped ones
        assertTrue(ex instanceof IllegalStateException || ex instanceof TransactionSystemException || ex.getCause() instanceof IllegalStateException);
    }
}
