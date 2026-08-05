package com.eneik.generated.services;

import com.eneik.generated.models.persistence.AcademicTerm;
import com.eneik.generated.models.persistence.ScholarshipRecord;
import com.eneik.generated.models.persistence.StudentPerformance;
import com.eneik.generated.repositories.ScholarshipRecordRepository;
import com.eneik.generated.repositories.StudentPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ScholarshipCalculationService {

    private final ScholarshipRecordRepository scholarshipRepository;
    private final StudentPerformanceRepository studentPerformanceRepository;

    @Autowired
    public ScholarshipCalculationService(
            ScholarshipRecordRepository scholarshipRepository,
            StudentPerformanceRepository studentPerformanceRepository) {
        this.scholarshipRepository = scholarshipRepository;
        this.studentPerformanceRepository = studentPerformanceRepository;
    }

    @Transactional
    public List<ScholarshipRecord> calculateAndPersistScholarships(AcademicTerm term) {
        // Check if scholarship records already exist for this term
        List<ScholarshipRecord> existing = scholarshipRepository.findByTermId(term.getId());
        if (!existing.isEmpty()) {
            return existing;
        }

        List<StudentPerformance> performances = studentPerformanceRepository.findAll();
        List<ScholarshipRecord> calculated = new ArrayList<>();

        for (StudentPerformance perf : performances) {
            BigDecimal gpa = perf.getGpa();
            BigDecimal amount;

            if (gpa.compareTo(new BigDecimal("4.50")) >= 0) {
                amount = new BigDecimal("20000.0000");
            } else if (gpa.compareTo(new BigDecimal("4.00")) >= 0) {
                amount = new BigDecimal("15000.0000");
            } else if (gpa.compareTo(new BigDecimal("3.00")) >= 0) {
                amount = new BigDecimal("10000.0000");
            } else {
                amount = BigDecimal.ZERO;
            }

            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                String id = UUID.randomUUID().toString();
                LocalDateTime calculatedAt = LocalDateTime.now();

                // Format amount to 4 decimal places for hashing consistency
                String amountStr = amount.setScale(4, RoundingMode.HALF_UP).toPlainString();
                String hashInput = perf.getStudentId() + ":" + term.getId() + ":" + amountStr;
                String hashChecksum = calculateSha256(hashInput);

                ScholarshipRecord record = new ScholarshipRecord(
                        id,
                        perf.getStudentId(),
                        perf.getStudentName(),
                        term,
                        amount,
                        calculatedAt,
                        hashChecksum
                );

                calculated.add(scholarshipRepository.save(record));
            }
        }

        return calculated;
    }

    public static String calculateSha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
