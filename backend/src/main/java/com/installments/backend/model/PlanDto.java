package com.installments.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PlanDto {

    private String id;

    @NotBlank(message = "Customer name is required")
    private String customer;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than zero")
    private Double totalAmount;

    @NotNull(message = "Installments count is required")
    @Positive(message = "Installments must be at least 1")
    private Integer installments;

    private Double monthlyDue;

    private String status;

    public PlanDto() {
    }

    public PlanDto(String id, String customer, Double totalAmount, Integer installments, Double monthlyDue, String status) {
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
