package com.installments.backend.repository;

import com.installments.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPlanId(String planId);

    @org.springframework.transaction.annotation.Transactional
    void deleteByPlanId(String planId);
}
