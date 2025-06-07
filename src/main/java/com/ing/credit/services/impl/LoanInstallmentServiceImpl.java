package com.ing.credit.services.impl;

import com.ing.credit.dao.repositories.LoanRepository;
import com.ing.credit.dtos.responses.LoanInstallmentResponse;
import com.ing.credit.services.LoanInstallmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanInstallmentServiceImpl implements LoanInstallmentService {

    private final LoanRepository loanRepository;

    @Override
    public List<LoanInstallmentResponse> listInstallments(UUID loanId) {
        var loan = loanRepository
                .findWithInstallmentsById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan installments not found"));
        return loan
                .getInstallments()
                .stream()
                .map(i -> new LoanInstallmentResponse(i.getId(), i.getAmount(), i.getPaidAmount(), i.getDueDate(), i.getPaymentDate(), i.getIsPaid()))
                .toList();

    }

}
