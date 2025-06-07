package com.ing.credit.controllers;

import com.ing.credit.dtos.requests.CreateCustomerRequest;
import com.ing.credit.dtos.responses.CreateCustomerResponse;
import com.ing.credit.dtos.responses.CustomerResponse;
import com.ing.credit.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
@Tag(name = "Customer Rest Controller")
@Slf4j
public class CustomerRestController {

    private final CustomerService customerService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Customer (ROLE: ADMIN)")
    @PostMapping("/customers")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody @Valid CreateCustomerRequest registerUserRequest) {
        var response = customerService.createCustomer(registerUserRequest.username(),
                registerUserRequest.password(),
                registerUserRequest.firstName(),
                registerUserRequest.lastName(),
                registerUserRequest.creditLimit());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List Customers (ROLE: ADMIN)")
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponse>> listCustomers() {
        var response = customerService.listCustomers();
        return ResponseEntity.ok(response);
    }

}
