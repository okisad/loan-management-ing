package com.ing.credit.controllers;

import com.ing.credit.config.JwtTokenContext;
import com.ing.credit.dtos.responses.LoanInstallmentResponse;
import com.ing.credit.services.LoanInstallmentService;
import com.ing.credit.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
@Tag(name = "Loan Installment Rest Controller")
@Slf4j
public class LoanInstallmentRestController {

    private final LoanInstallmentService loanInstallmentService;
    private final LoanService loanService;


    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Operation(summary = "List INstallments of Loan (ROLE: ADMIN, CUSTOMER)")
    @GetMapping("/loans/{loanId}/installments")
    public ResponseEntity<List<LoanInstallmentResponse>> listInstallmentsOfLoan(@PathVariable UUID loanId) {
        var customerIdOfToken = JwtTokenContext.getCustomerId();
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) || loanService.isCustomerOwnerOfLoan(loanId, customerIdOfToken)) {
            var installments = loanInstallmentService.listInstallments(loanId);
            return ResponseEntity.ok(installments);
        } else
            throw new AccessDeniedException("Access denied");
    }

}
