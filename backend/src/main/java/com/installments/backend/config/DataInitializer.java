package com.installments.backend.config;

import com.installments.backend.model.InstallmentPlan;
import com.installments.backend.model.User;
import com.installments.backend.repository.InstallmentPlanRepository;
import com.installments.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final InstallmentPlanRepository planRepository;
    private final com.installments.backend.repository.PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           InstallmentPlanRepository planRepository,
                           com.installments.backend.repository.PaymentRepository paymentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed users with different roles
        seedUserIfMissing("testuser@example.com", "testpassword", "ROLE_USER");
        seedUserIfMissing("admin@example.com", "adminpassword123", "ROLE_ADMIN");
        seedUserIfMissing("auditor@example.com", "auditor123", "ROLE_AUDITOR");

        // Seed / Reset baseline installment plans for test consistency
        planRepository.save(new InstallmentPlan("PLAN-101", "Alice Smith", 1200.00, 12, 100.00, "ACTIVE"));
        planRepository.save(new InstallmentPlan("PLAN-102", "Bob Johnson", 600.00, 6, 100.00, "PAID"));
        planRepository.save(new InstallmentPlan("PLAN-103", "Charlie Brown", 2400.00, 24, 100.00, "PENDING"));

        // Seed initial payment transaction for PAID plan PLAN-102
        if (paymentRepository.findByPlanId("PLAN-102").isEmpty()) {
            paymentRepository.save(new com.installments.backend.model.Payment(
                    "PLAN-102",
                    600.00,
                    "CREDIT_CARD",
                    "TXN-INIT-102"
            ));
        }
    }

    private void seedUserIfMissing(String username, String rawPassword, String role) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(new User(username, passwordEncoder.encode(rawPassword), role));
        }
    }
}
