package com.ing.credit.dtos.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCustomerRequest(
        @NotNull
        String username,
        @NotNull
        String password,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        BigDecimal creditLimit
) {
}
