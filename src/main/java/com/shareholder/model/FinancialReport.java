package com.shareholder.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancialReport {
    private int reportId;
    private int reportYear;
    private int reportQuarter; // 1-4
    private BigDecimal revenue;
    private BigDecimal profitBeforeTax;
    private BigDecimal profitAfterTax;
    private BigDecimal shortTermDebt;
    private BigDecimal longTermDebt;
    private BigDecimal eps;
    private BigDecimal pe;
    private BigDecimal roe;
    private BigDecimal roa;
    private LocalDateTime createdAt;
    /** user_id cua ADMIN da tao bao cao nay. Co the NULL voi du lieu cu tao truoc khi co cot nay.
     *  KHONG bi ghi de khi sua bao cao (update giu nguyen nguoi tao ban dau). */
    private Integer createdBy;

    public FinancialReport() {}

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getReportYear() { return reportYear; }
    public void setReportYear(int reportYear) { this.reportYear = reportYear; }

    public int getReportQuarter() { return reportQuarter; }
    public void setReportQuarter(int reportQuarter) { this.reportQuarter = reportQuarter; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

    public BigDecimal getProfitBeforeTax() { return profitBeforeTax; }
    public void setProfitBeforeTax(BigDecimal profitBeforeTax) { this.profitBeforeTax = profitBeforeTax; }

    public BigDecimal getProfitAfterTax() { return profitAfterTax; }
    public void setProfitAfterTax(BigDecimal profitAfterTax) { this.profitAfterTax = profitAfterTax; }

    public BigDecimal getShortTermDebt() { return shortTermDebt; }
    public void setShortTermDebt(BigDecimal shortTermDebt) { this.shortTermDebt = shortTermDebt; }

    public BigDecimal getLongTermDebt() { return longTermDebt; }
    public void setLongTermDebt(BigDecimal longTermDebt) { this.longTermDebt = longTermDebt; }

    public BigDecimal getEps() { return eps; }
    public void setEps(BigDecimal eps) { this.eps = eps; }

    public BigDecimal getPe() { return pe; }
    public void setPe(BigDecimal pe) { this.pe = pe; }

    public BigDecimal getRoe() { return roe; }
    public void setRoe(BigDecimal roe) { this.roe = roe; }

    public BigDecimal getRoa() { return roa; }
    public void setRoa(BigDecimal roa) { this.roa = roa; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
}
