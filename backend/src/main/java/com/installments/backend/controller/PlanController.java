package com.installments.backend.controller;

import com.installments.backend.model.InstallmentPlan;
import com.installments.backend.model.PlanDto;
import com.installments.backend.repository.InstallmentPlanRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class PlanController {

    private final InstallmentPlanRepository planRepository;
    private final com.installments.backend.repository.PaymentRepository paymentRepository;

    public PlanController(InstallmentPlanRepository planRepository,
                          com.installments.backend.repository.PaymentRepository paymentRepository) {
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public ResponseEntity<List<InstallmentPlan>> getAllPlans() {
        return ResponseEntity.ok(planRepository.findAll());
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<com.installments.backend.model.Payment>> getPaymentsForPlan(@PathVariable String id) {
        return ResponseEntity.ok(paymentRepository.findByPlanId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallmentPlan> getPlanById(@PathVariable String id) {
        return planRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<InstallmentPlan> createPlan(@Valid @RequestBody PlanDto dto) {
        String planId = (dto.getId() != null && !dto.getId().trim().isEmpty())
                ? dto.getId().trim()
                : "PLAN-" + (100 + planRepository.count() + 1);

        if (planRepository.existsById(planId)) {
            planRepository.deleteById(planId);
        }

        double monthlyDue = (dto.getMonthlyDue() != null && dto.getMonthlyDue() > 0)
                ? dto.getMonthlyDue()
                : Math.round((dto.getTotalAmount() / dto.getInstallments()) * 100.0) / 100.0;

        String status = (dto.getStatus() != null && !dto.getStatus().trim().isEmpty())
                ? dto.getStatus().trim().toUpperCase()
                : "ACTIVE";

        InstallmentPlan newPlan = new InstallmentPlan(
                planId,
                dto.getCustomer(),
                dto.getTotalAmount(),
                dto.getInstallments(),
                monthlyDue,
                status
        );

        InstallmentPlan saved = planRepository.save(newPlan);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstallmentPlan> updatePlan(@PathVariable String id, @RequestBody PlanDto dto) {
        return planRepository.findById(id)
                .map(existing -> {
                    if (dto.getCustomer() != null && !dto.getCustomer().trim().isEmpty()) {
                        existing.setCustomer(dto.getCustomer());
                    }
                    if (dto.getTotalAmount() != null && dto.getTotalAmount() > 0) {
                        existing.setTotalAmount(dto.getTotalAmount());
                    }
                    if (dto.getInstallments() != null && dto.getInstallments() > 0) {
                        existing.setInstallments(dto.getInstallments());
                    }
                    if (dto.getMonthlyDue() != null && dto.getMonthlyDue() > 0) {
                        existing.setMonthlyDue(dto.getMonthlyDue());
                    } else if (dto.getTotalAmount() != null && dto.getInstallments() != null) {
                        existing.setMonthlyDue(Math.round((dto.getTotalAmount() / dto.getInstallments()) * 100.0) / 100.0);
                    }
                    if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
                        String newStatus = dto.getStatus().trim().toUpperCase();
                        existing.setStatus(newStatus);
                        if ("PAID".equalsIgnoreCase(newStatus) && paymentRepository.findByPlanId(id).isEmpty()) {
                            paymentRepository.save(new com.installments.backend.model.Payment(
                                    id,
                                    existing.getTotalAmount(),
                                    "API_PAYMENT",
                                    "TXN-API-" + System.currentTimeMillis()
                            ));
                        }
                    }
                    return ResponseEntity.ok(planRepository.save(existing));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Map<String, Object>> deletePlan(@PathVariable String id) {
        return planRepository.findById(id)
                .map(existing -> {
                    paymentRepository.deleteByPlanId(id);
                    planRepository.delete(existing);
                    return ResponseEntity.ok(Map.<String, Object>of(
                            "message", "Plan deleted successfully",
                            "id", id,
                            "status", "DELETED"
                    ));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found with id: " + id));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetSeedData() {
        planRepository.save(new InstallmentPlan("PLAN-101", "Alice Smith", 1200.00, 12, 100.00, "ACTIVE"));
        planRepository.save(new InstallmentPlan("PLAN-102", "Bob Johnson", 600.00, 6, 100.00, "PAID"));
        planRepository.save(new InstallmentPlan("PLAN-103", "Charlie Brown", 2400.00, 24, 100.00, "PENDING"));

        if (paymentRepository.findByPlanId("PLAN-102").isEmpty()) {
            paymentRepository.save(new com.installments.backend.model.Payment(
                    "PLAN-102",
                    600.00,
                    "CREDIT_CARD",
                    "TXN-INIT-102"
            ));
        }
        return ResponseEntity.ok(Map.of("message", "Baseline installment plans and payments reset successfully"));
    }
}
