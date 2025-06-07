package com.ing.credit.controllers;

import com.ing.credit.config.JwtTokenContext;
import com.ing.credit.dtos.requests.CreateLoanRequest;
import com.ing.credit.dtos.requests.PayLoanRequest;
import com.ing.credit.dtos.responses.CreateLoanResponse;
import com.ing.credit.dtos.responses.LoanResponse;
import com.ing.credit.dtos.responses.PayLoanResponse;
import com.ing.credit.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
@Tag(name = "Loan Rest Controller")
@Slf4j
public class LoanRestController {

    private final LoanService loanService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Loan for Customer (ROLE: ADMIN)")
    @PostMapping("/loans")
    public ResponseEntity<CreateLoanResponse> createLoan(@RequestBody @Valid CreateLoanRequest createLoanRequest) {
        var response = loanService.createLoan(
                createLoanRequest.customerId(),
                createLoanRequest.amount(),
                createLoanRequest.interestRate(),
                createLoanRequest.numberOfInstallments()
        );
        return ResponseEntity.ok(response);
    }

    //ADMIN check
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Operation(summary = "List Loans of Customer (ROLE: ADMIN, CUSTOMER)")
    @GetMapping("/customers/{customerId}/loans")
    public ResponseEntity<List<LoanResponse>> listLoans(@PathVariable("customerId") UUID customerId) {
        var customerIdOfToken = JwtTokenContext.getCustomerId();
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) || customerId.equals(customerIdOfToken)){
            var loans = loanService.listLoans(customerId);
            return ResponseEntity.ok(loans);
        }
        else{
            throw new AccessDeniedException("Access denied");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Operation(summary = "Pay Loan for Installment (ROLE: ADMIN, CUSTOMER)")
    @PostMapping("/loans/{loanId}/payment")
    public ResponseEntity<PayLoanResponse> payLoan(@PathVariable UUID loanId, @RequestBody @Valid PayLoanRequest payLoanRequest) {
        var customerIdOfToken = JwtTokenContext.getCustomerId();
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) || loanService.isCustomerOwnerOfLoan(loanId, customerIdOfToken)) {
            var response = loanService.payLoan(loanId, payLoanRequest.amount());
            return ResponseEntity.ok(response);
        }
        throw new AccessDeniedException("Access denied");

    }

}
