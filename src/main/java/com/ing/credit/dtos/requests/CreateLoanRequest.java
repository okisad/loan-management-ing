package com.ing.credit.dtos.requests;

import com.ing.credit.common.validations.ValidInstallment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanRequest(
        @NotNull
        UUID customerId,
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false, message = "amount should be greater than 0")
        BigDecimal amount,
        @NotNull
        @DecimalMin(value = "0.0", message = "interestRate should be greater than 0")
        BigDecimal interestRate,
        @Positive
        @ValidInstallment
        int numberOfInstallments
) {
}
