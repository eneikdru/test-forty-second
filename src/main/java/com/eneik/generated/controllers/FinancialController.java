package com.eneik.generated.controllers;

import com.eneik.generated.dtos.*;
import com.eneik.generated.models.persistence.*;
import com.eneik.generated.repositories.*;
import com.eneik.generated.services.ScholarshipCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/financial")
public class FinancialController {

    private final AcademicTermRepository termRepository;
    private final BudgetRecordRepository budgetRepository;
    private final WorkloadRecordRepository workloadRepository;
    private final ScholarshipRecordRepository scholarshipRepository;
    private final ScholarshipCalculationService scholarshipService;

    @Autowired
    public FinancialController(
            AcademicTermRepository termRepository,
            BudgetRecordRepository budgetRepository,
            WorkloadRecordRepository workloadRepository,
            ScholarshipRecordRepository scholarshipRepository,
            ScholarshipCalculationService scholarshipService) {
        this.termRepository = termRepository;
        this.budgetRepository = budgetRepository;
        this.workloadRepository = workloadRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.scholarshipService = scholarshipService;
    }

    @GetMapping("/terms")
    public List<AcademicTermDto> getTerms() {
        return termRepository.findAll().stream()
                .map(t -> new AcademicTermDto(t.getId(), t.getName(), t.getStartDate(), t.getEndDate()))
                .collect(Collectors.toList());
    }

    @PostMapping("/budgets")
    public ResponseEntity<?> submitBudget(
            @RequestBody BudgetSubmissionRequest request,
            @RequestHeader(value = "X-Role", required = false) String role) {

        if (role != null && role.equalsIgnoreCase("TEACHER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiErrorDto("ACCESS_DENIED", "Teachers are not authorized to view or modify budget data.", LocalDateTime.now(), null));
        }

        if (request.getTermId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorDto("VALIDATION_FAILED", "termId is required.", LocalDateTime.now(), null));
        }

        AcademicTerm term = termRepository.findById(request.getTermId()).orElse(null);
        if (term == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorDto("NOT_FOUND", "Academic term not found.", LocalDateTime.now(), null));
        }

        BigDecimal amount = request.getAmount();
        BigDecimal allocatedAmount = request.getAllocatedAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0 ||
            allocatedAmount == null || allocatedAmount.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorDto("VALIDATION_FAILED", "Amounts must not be negative.", LocalDateTime.now(), null));
        }

        if (allocatedAmount.compareTo(amount) > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorDto("VALIDATION_FAILED", "Allocated amount cannot exceed total budget amount.", LocalDateTime.now(), null));
        }

        BudgetRecord record = new BudgetRecord(
                UUID.randomUUID().toString(),
                term,
                request.getCategory(),
                amount,
                allocatedAmount
        );

        BudgetRecord saved = budgetRepository.save(record);
        BudgetRecordDto dto = new BudgetRecordDto(
                saved.getId(),
                saved.getTerm().getId(),
                saved.getCategory(),
                saved.getAmount(),
                saved.getAllocatedAmount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/budgets")
    public ResponseEntity<?> getBudgets(
            @RequestParam(value = "termId", required = false) String termId,
            @RequestHeader(value = "X-Role", required = false) String role) {

        if (role != null && role.equalsIgnoreCase("TEACHER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiErrorDto("ACCESS_DENIED", "Teachers are not authorized to view or modify budget data.", LocalDateTime.now(), null));
        }

        List<BudgetRecord> records;
        if (termId != null && !termId.trim().isEmpty()) {
            records = budgetRepository.findByTermId(termId);
        } else {
            records = budgetRepository.findAll();
        }

        List<BudgetRecordDto> dtos = records.stream()
                .map(b -> new BudgetRecordDto(b.getId(), b.getTerm().getId(), b.getCategory(), b.getAmount(), b.getAllocatedAmount()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/workloads")
    public ResponseEntity<?> assignWorkload(@RequestBody WorkloadAssignmentRequest request) {
        if (request.getTermId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorDto("VALIDATION_FAILED", "termId is required.", LocalDateTime.now(), null));
        }

        AcademicTerm term = termRepository.findById(request.getTermId()).orElse(null);
        if (term == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorDto("NOT_FOUND", "Academic term not found.", LocalDateTime.now(), null));
        }

        Integer plannedHours = request.getPlannedHours();
        BigDecimal hourlyRate = request.getHourlyRate();

        if (plannedHours == null || plannedHours < 0 ||
            hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorDto("VALIDATION_FAILED", "Planned hours and hourly rate must not be negative.", LocalDateTime.now(), null));
        }

        WorkloadRecord record = new WorkloadRecord(
                UUID.randomUUID().toString(),
                term,
                request.getInstructorName(),
                plannedHours,
                hourlyRate
        );

        WorkloadRecord saved = workloadRepository.save(record);
        WorkloadRecordDto dto = new WorkloadRecordDto(
                saved.getId(),
                saved.getTerm().getId(),
                saved.getInstructorName(),
                saved.getPlannedHours(),
                saved.getHourlyRate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/workloads")
    public List<WorkloadRecordDto> getWorkloads(@RequestParam(value = "termId", required = false) String termId) {
        List<WorkloadRecord> records;
        if (termId != null && !termId.trim().isEmpty()) {
            records = workloadRepository.findByTermId(termId);
        } else {
            records = workloadRepository.findAll();
        }

        return records.stream()
                .map(w -> new WorkloadRecordDto(w.getId(), w.getTerm().getId(), w.getInstructorName(), w.getPlannedHours(), w.getHourlyRate()))
                .collect(Collectors.toList());
    }

    @GetMapping("/reports/summary")
    public ResponseEntity<?> getFinancialSummary(@RequestParam("termId") String termId) {
        if (termId == null || termId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorDto("VALIDATION_FAILED", "termId query parameter is required.", LocalDateTime.now(), null));
        }

        AcademicTerm term = termRepository.findById(termId).orElse(null);
        if (term == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorDto("NOT_FOUND", "Academic term not found.", LocalDateTime.now(), null));
        }

        // Run/ensure scholarship calculations for the term
        scholarshipService.calculateAndPersistScholarships(term);

        // Compute total values
        List<BudgetRecord> budgetRecords = budgetRepository.findByTermId(termId);
        List<WorkloadRecord> workloadRecords = workloadRepository.findByTermId(termId);
        List<ScholarshipRecord> scholarshipRecords = scholarshipRepository.findByTermId(termId);

        BigDecimal totalBudgetAmount = budgetRecords.stream()
                .map(BudgetRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAllocatedAmount = budgetRecords.stream()
                .map(BudgetRecord::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWorkloadCost = workloadRecords.stream()
                .map(w -> w.getHourlyRate().multiply(BigDecimal.valueOf(w.getPlannedHours())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalScholarshipPayout = scholarshipRecords.stream()
                .map(ScholarshipRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingBudget = totalBudgetAmount.subtract(totalAllocatedAmount);

        FinancialReportSummaryDto summaryDto = new FinancialReportSummaryDto(
                term.getId(),
                term.getName(),
                totalBudgetAmount,
                totalAllocatedAmount,
                totalWorkloadCost,
                totalScholarshipPayout,
                remainingBudget
        );

        return ResponseEntity.ok(summaryDto);
    }
}
