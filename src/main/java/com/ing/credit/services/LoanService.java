package com.ing.credit.services;

import com.ing.credit.dtos.responses.CreateLoanResponse;
import com.ing.credit.dtos.responses.LoanResponse;
import com.ing.credit.dtos.responses.PayLoanResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Validated
public interface LoanService {

    CreateLoanResponse createLoan(@NotNull UUID customerId,
                                  @NotNull BigDecimal amount,
                                  @NotNull BigDecimal interestRate,
                                  @NotNull Integer numberOfInstallments);

    List<LoanResponse> listLoans(@NotNull UUID customerId);

    PayLoanResponse payLoan(@NotNull UUID loanId, @NotNull BigDecimal amount);

    boolean isCustomerOwnerOfLoan(@NotNull UUID loanId,  @NotNull UUID customerId);

}
