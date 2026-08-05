package com.eneik.generated;

import com.eneik.generated.controllers.FinancialController;
import com.eneik.generated.controllers.FinancialExceptionHandler;
import com.eneik.generated.models.persistence.AcademicTerm;
import com.eneik.generated.models.persistence.BudgetRecord;
import com.eneik.generated.models.persistence.ScholarshipRecord;
import com.eneik.generated.models.persistence.StudentPerformance;
import com.eneik.generated.models.persistence.WorkloadRecord;
import com.eneik.generated.repositories.AcademicTermRepository;
import com.eneik.generated.repositories.BudgetRecordRepository;
import com.eneik.generated.repositories.ScholarshipRecordRepository;
import com.eneik.generated.repositories.StudentPerformanceRepository;
import com.eneik.generated.repositories.WorkloadRecordRepository;
import com.eneik.generated.services.ScholarshipCalculationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionSystemException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private StudentPerformanceRepository studentPerformanceRepository;

    @Autowired
    private ScholarshipCalculationService scholarshipService;

    @Autowired
    private EntityManager entityManager;

    private AcademicTerm term;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        scholarshipRepository.deleteAll();
        workloadRepository.deleteAll();
        budgetRepository.deleteAll();
        termRepository.deleteAll();
        studentPerformanceRepository.deleteAll();

        // Initialize term with valid dates
        term = new AcademicTerm(
                UUID.randomUUID().toString(),
                "Fall 2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 31)
        );
        termRepository.saveAndFlush(term);

        // Standalone MockMvc Setup
        FinancialController controller = new FinancialController(
                termRepository,
                budgetRepository,
                workloadRepository,
                scholarshipRepository,
                scholarshipService
        );
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new FinancialExceptionHandler())
                .build();
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

    @Test
    public void testScholarshipCalculationUnitLogic() {
        // Prepare diverse student performance GPA records
        studentPerformanceRepository.saveAndFlush(new StudentPerformance("ST-EXC", "Ivanov Excellent", new BigDecimal("4.85")));
        studentPerformanceRepository.saveAndFlush(new StudentPerformance("ST-GD", "Petrov Good", new BigDecimal("4.20")));
        studentPerformanceRepository.saveAndFlush(new StudentPerformance("ST-SAT", "Sidorov Satisfactory", new BigDecimal("3.50")));
        studentPerformanceRepository.saveAndFlush(new StudentPerformance("ST-PR", "Kuznetsov Poor", new BigDecimal("2.90")));

        // Execute scholarship calculation
        List<ScholarshipRecord> calculated = scholarshipService.calculateAndPersistScholarships(term);

        // Excellent: GPA >= 4.5 -> 20,000.00
        // Good: GPA >= 4.0 -> 15,000.00
        // Satisfactory: GPA >= 3.0 -> 10,000.00
        // Poor: GPA < 3.0 -> 0.00 (not persisted)
        assertEquals(3, calculated.size());

        ScholarshipRecord excRecord = calculated.stream().filter(s -> s.getStudentId().equals("ST-EXC")).findFirst().orElse(null);
        assertNotNull(excRecord);
        assertEquals(0, excRecord.getAmount().compareTo(new BigDecimal("20000.0000")));
        String excExpectedHash = ScholarshipCalculationService.calculateSha256("ST-EXC:" + term.getId() + ":" + excRecord.getAmount().setScale(4, RoundingMode.HALF_UP).toPlainString());
        assertEquals(excExpectedHash, excRecord.getHashChecksum());

        ScholarshipRecord gdRecord = calculated.stream().filter(s -> s.getStudentId().equals("ST-GD")).findFirst().orElse(null);
        assertNotNull(gdRecord);
        assertEquals(0, gdRecord.getAmount().compareTo(new BigDecimal("15000.0000")));
        String gdExpectedHash = ScholarshipCalculationService.calculateSha256("ST-GD:" + term.getId() + ":" + gdRecord.getAmount().setScale(4, RoundingMode.HALF_UP).toPlainString());
        assertEquals(gdExpectedHash, gdRecord.getHashChecksum());

        ScholarshipRecord satRecord = calculated.stream().filter(s -> s.getStudentId().equals("ST-SAT")).findFirst().orElse(null);
        assertNotNull(satRecord);
        assertEquals(0, satRecord.getAmount().compareTo(new BigDecimal("10000.0000")));
        String satExpectedHash = ScholarshipCalculationService.calculateSha256("ST-SAT:" + term.getId() + ":" + satRecord.getAmount().setScale(4, RoundingMode.HALF_UP).toPlainString());
        assertEquals(satExpectedHash, satRecord.getHashChecksum());

        ScholarshipRecord prRecord = calculated.stream().filter(s -> s.getStudentId().equals("ST-PR")).findFirst().orElse(null);
        assertNull(prRecord);
    }

    @Test
    public void testTeacherAuthorizationForBudgets() throws Exception {
        // 1. GET /api/v1/financial/budgets with X-Role: TEACHER should return 403 Forbidden
        mockMvc.perform(get("/api/v1/financial/budgets")
                        .header("X-Role", "TEACHER"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("Teachers are not authorized to view or modify budget data."));

        // 2. POST /api/v1/financial/budgets with X-Role: TEACHER should return 403 Forbidden
        String body = "{\"termId\":\"" + term.getId() + "\",\"category\":\"Salaries\",\"amount\":1000.0,\"allocatedAmount\":50.0}";
        mockMvc.perform(post("/api/v1/financial/budgets")
                        .header("X-Role", "TEACHER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("Teachers are not authorized to view or modify budget data."));
    }

    @Test
    public void testAccountantGeneratesScholarshipReport() throws Exception {
        // Setup mock student performance
        studentPerformanceRepository.saveAndFlush(new StudentPerformance("ST-ACCT-1", "A", new BigDecimal("4.80")));
        studentPerformanceRepository.saveAndFlush(new StudentPerformance("ST-ACCT-2", "B", new BigDecimal("4.10")));

        // Generate budget and workloads for the term
        budgetRepository.saveAndFlush(new BudgetRecord(UUID.randomUUID().toString(), term, "Scholarships", new BigDecimal("500000.0000"), new BigDecimal("350000.0000")));
        workloadRepository.saveAndFlush(new WorkloadRecord(UUID.randomUUID().toString(), term, "Dr. Ivanov", 100, new BigDecimal("150.0000")));

        // Execute report generation via Accountant role
        mockMvc.perform(get("/api/v1/financial/reports/summary")
                        .param("termId", term.getId())
                        .header("X-Role", "Accountant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.termId").value(term.getId()))
                .andExpect(jsonPath("$.termName").value(term.getName()))
                .andExpect(jsonPath("$.totalBudgetAmount").value(500000.00))
                .andExpect(jsonPath("$.totalAllocatedAmount").value(350000.00))
                .andExpect(jsonPath("$.totalWorkloadCost").value(15000.00)) // 100 * 150 = 15000
                .andExpect(jsonPath("$.totalScholarshipPayout").value(35000.00)) // 20000 + 15000 = 35000
                .andExpect(jsonPath("$.remainingBudget").value(150000.00));
    }
}
