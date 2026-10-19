package com.installments.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "installment_plans")
public class InstallmentPlan {

    @Id
    @Column(nullable = false, length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String customer;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Integer installments;

    @Column(name = "monthly_due", nullable = false)
    private Double monthlyDue;

    @Column(nullable = false, length = 20)
    private String status;

    public InstallmentPlan() {
    }

    public InstallmentPlan(String id, String customer, Double totalAmount, Integer installments, Double monthlyDue, String status) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.installments = installments;
        this.monthlyDue = monthlyDue;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getMonthlyDue() {
        return monthlyDue;
    }

    public void setMonthlyDue(Double monthlyDue) {
        this.monthlyDue = monthlyDue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
