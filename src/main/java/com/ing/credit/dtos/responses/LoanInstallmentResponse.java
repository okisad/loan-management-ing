package com.ing.credit.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanInstallmentResponse(
        UUID id,
        BigDecimal amount,
        BigDecimal paidAmount,
        LocalDate dueDate,
        LocalDateTime paymentDate,
        Boolean isPaid
){
}
