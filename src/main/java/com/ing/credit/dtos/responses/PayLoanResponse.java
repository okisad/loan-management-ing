package com.ing.credit.dtos.responses;

import java.math.BigDecimal;

public record PayLoanResponse(
        int numberOfPaidInstallments,
        int totalNumberOfPaidInstallments,
        int numberOfRemainingInstallments,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        boolean isLoanCompleted
) {
}
