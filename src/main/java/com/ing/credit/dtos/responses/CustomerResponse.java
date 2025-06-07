package com.ing.credit.dtos.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        BigDecimal creditLimit,
        BigDecimal usedCredit
) {
}
