package com.eneik.generated.dtos;

import java.math.BigDecimal;

public class FinancialReportSummaryDto {
    private String termId;
    private String termName;
    private BigDecimal totalBudgetAmount;
    private BigDecimal totalAllocatedAmount;
    private BigDecimal totalWorkloadCost;
    private BigDecimal totalScholarshipPayout;
    private BigDecimal remainingBudget;

    public FinancialReportSummaryDto() {}

    public FinancialReportSummaryDto(String termId, String termName, BigDecimal totalBudgetAmount, BigDecimal totalAllocatedAmount, BigDecimal totalWorkloadCost, BigDecimal totalScholarshipPayout, BigDecimal remainingBudget) {
        this.termId = termId;
        this.termName = termName;
        this.totalBudgetAmount = totalBudgetAmount;
        this.totalAllocatedAmount = totalAllocatedAmount;
        this.totalWorkloadCost = totalWorkloadCost;
        this.totalScholarshipPayout = totalScholarshipPayout;
        this.remainingBudget = remainingBudget;
    }

    public String getTermId() { return termId; }
    public void setTermId(String termId) { this.termId = termId; }

    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }

    public BigDecimal getTotalBudgetAmount() { return totalBudgetAmount; }
    public void setTotalBudgetAmount(BigDecimal totalBudgetAmount) { this.totalBudgetAmount = totalBudgetAmount; }

    public BigDecimal getTotalAllocatedAmount() { return totalAllocatedAmount; }
    public void setTotalAllocatedAmount(BigDecimal totalAllocatedAmount) { this.totalAllocatedAmount = totalAllocatedAmount; }

    public BigDecimal getTotalWorkloadCost() { return totalWorkloadCost; }
    public void setTotalWorkloadCost(BigDecimal totalWorkloadCost) { this.totalWorkloadCost = totalWorkloadCost; }

    public BigDecimal getTotalScholarshipPayout() { return totalScholarshipPayout; }
    public void setTotalScholarshipPayout(BigDecimal totalScholarshipPayout) { this.totalScholarshipPayout = totalScholarshipPayout; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }
}
