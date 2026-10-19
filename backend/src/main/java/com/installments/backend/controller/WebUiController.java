package com.installments.backend.controller;

import com.installments.backend.model.InstallmentPlan;
import com.installments.backend.repository.InstallmentPlanRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.Pattern;

@Controller
public class WebUiController {

    private final InstallmentPlanRepository planRepository;
    private final com.installments.backend.repository.UserRepository userRepository;
    private final com.installments.backend.repository.PaymentRepository paymentRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public WebUiController(InstallmentPlanRepository planRepository,
                           com.installments.backend.repository.UserRepository userRepository,
                           com.installments.backend.repository.PaymentRepository paymentRepository,
                           org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam(value = "username", required = false, defaultValue = "") String username,
                              @RequestParam(value = "password", required = false, defaultValue = "") String password,
                              HttpSession session,
                              Model model) {
        String trimmedUser = username.trim();
        String trimmedPass = password.trim();

        if (trimmedUser.isEmpty()) {
            model.addAttribute("errorMessage", "Username is required!");
            model.addAttribute("username", trimmedUser);
            return "login";
        }

        if (!EMAIL_PATTERN.matcher(trimmedUser).matches()) {
            model.addAttribute("errorMessage", "Username must be a valid email address!");
            model.addAttribute("username", trimmedUser);
            return "login";
        }

        if (trimmedPass.isEmpty()) {
            model.addAttribute("errorMessage", "Password is required!");
            model.addAttribute("username", trimmedUser);
            return "login";
        }

        return userRepository.findByUsername(trimmedUser)
                .filter(u -> passwordEncoder.matches(trimmedPass, u.getPassword()))
                .map(u -> {
                    session.setAttribute("user", u.getUsername());
                    session.setAttribute("role", u.getRole());
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("errorMessage", "Invalid username or password!");
                    model.addAttribute("username", trimmedUser);
                    return "login";
                });
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        List<InstallmentPlan> plans = planRepository.findAll();
        model.addAttribute("plans", plans);
        model.addAttribute("currentUser", session.getAttribute("user"));
        return "dashboard";
    }

    @PostMapping("/dashboard/create")
    @org.springframework.transaction.annotation.Transactional
    public String createPlanFromUi(@RequestParam("id") String id,
                                   @RequestParam("customer") String customer,
                                   @RequestParam("totalAmount") Double totalAmount,
                                   @RequestParam("installments") Integer installments,
                                   HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        String planId = id.trim();
        if (planRepository.existsById(planId)) {
            paymentRepository.deleteByPlanId(planId);
            planRepository.deleteById(planId);
        }
        double monthlyDue = (installments > 0)
                ? Math.round((totalAmount / installments) * 100.0) / 100.0
                : totalAmount;
        InstallmentPlan plan = new InstallmentPlan(planId, customer.trim(), totalAmount, installments, monthlyDue, "ACTIVE");
        planRepository.save(plan);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/pay/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String payPlan(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        planRepository.findById(id).ifPresent(plan -> {
            plan.setStatus("PAID");
            planRepository.save(plan);
            paymentRepository.save(new com.installments.backend.model.Payment(
                    id,
                    plan.getTotalAmount(),
                    "ONLINE_PORTAL",
                    "TXN-UI-" + System.currentTimeMillis()
            ));
        });
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/delete/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String deletePlanFromUi(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        paymentRepository.deleteByPlanId(id);
        planRepository.deleteById(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
