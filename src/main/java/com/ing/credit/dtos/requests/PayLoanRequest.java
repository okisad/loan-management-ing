package com.ing.credit.dtos.requests;

import java.math.BigDecimal;

public record PayLoanRequest(
        BigDecimal amount
) {
}
