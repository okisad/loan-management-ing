package com.ing.credit.services;

import com.ing.credit.dtos.responses.LoanInstallmentResponse;

import java.util.List;
import java.util.UUID;

public interface LoanInstallmentService {

    List<LoanInstallmentResponse> listInstallments(UUID loanId);

}
