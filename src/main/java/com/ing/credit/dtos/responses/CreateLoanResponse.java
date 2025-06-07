package com.ing.credit.dtos.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanResponse(
        UUID id,
        BigDecimal loanAmount,
        Integer numberOfInstallments
) {
}
