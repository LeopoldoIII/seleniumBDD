package com.installments.backend.repository;

import com.installments.backend.model.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, String> {
}
