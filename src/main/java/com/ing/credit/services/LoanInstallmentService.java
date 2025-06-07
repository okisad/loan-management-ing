package com.ing.credit.services;

import com.ing.credit.dtos.responses.LoanInstallmentResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public interface LoanInstallmentService {

    List<LoanInstallmentResponse> listInstallments(@NotNull  UUID loanId);

}
