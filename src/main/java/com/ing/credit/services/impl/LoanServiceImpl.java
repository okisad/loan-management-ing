package com.ing.credit.services.impl;

import com.ing.credit.config.LoanConfiguration;
import com.ing.credit.dao.entities.CustomerEntity;
import com.ing.credit.dao.entities.LoanEntity;
import com.ing.credit.dao.entities.LoanInstallmentEntity;
import com.ing.credit.dao.repositories.CustomerRepository;
import com.ing.credit.dao.repositories.LoanRepository;
import com.ing.credit.dtos.responses.CreateLoanResponse;
import com.ing.credit.dtos.responses.LoanResponse;
import com.ing.credit.dtos.responses.PayLoanResponse;
import com.ing.credit.services.LoanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanConfiguration loanConfiguration;
    private final Clock clock;

    @Override
    @Transactional
    public CreateLoanResponse createLoan(UUID customerId,
                                         BigDecimal amount,
                                         BigDecimal interestRate,
                                         Integer numberOfInstallments) {
        var customer = customerRepository.findById(customerId).orElseThrow();
        validateCreateLoan(amount, customer, interestRate, numberOfInstallments);
        var loan = LoanEntity.createLoan(customer, amount, interestRate, numberOfInstallments, LocalDate.now(clock));
        var createdLoan = loanRepository.save(loan);
        customer.increaseUsedCreditLimit(createdLoan.getLoanAmount());
        customerRepository.save(customer);
        log.info("loan has been created for customerId: {} with amount: {} and number of installment:{}", customerId, amount, numberOfInstallments);
        return new CreateLoanResponse(createdLoan.getId(), createdLoan.getLoanAmount(), createdLoan.getNumberOfInstallments());
    }

    private void validateCreateLoan(BigDecimal amount, CustomerEntity customer, BigDecimal interestRate, Integer numberOfInstallments) {
        if (customer.getAvailableCreditLimit().compareTo(amount) < 0) {
            throw new RuntimeException("Customer not enough available credit limit");
        }
        var allowedMinimumInterestRate = loanConfiguration.getAllowedMinimumInterestRate();
        var allowedMaximumInterestRate = loanConfiguration.getAllowedMaximumInterestRate();
        if (interestRate.compareTo(allowedMinimumInterestRate) < 0 || interestRate.compareTo(allowedMaximumInterestRate) > 0) {
            throw new RuntimeException("Interest rate must be between " + allowedMinimumInterestRate + " and " + allowedMaximumInterestRate);
        }
        var allowedInstallmentCounts = loanConfiguration.getAllowedInstallmentCounts();
        if (!allowedInstallmentCounts.contains(numberOfInstallments)) {
            throw new RuntimeException("Installments must be one of " + allowedInstallmentCounts.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }
    }

    @Override
    public List<LoanResponse> listLoans(UUID customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        return loanRepository
                .findByCustomerId(customerId)
                .stream()
                .map(l -> new LoanResponse(l.getId(), l.getLoanAmount(), l.getNumberOfInstallments(), l.getIsPaid()))
                .toList();
    }

    @Override
    @Transactional
    public PayLoanResponse payLoan(UUID loanId, BigDecimal amount) {
        var loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        var payableInstallments = loan.getInstallments()
                .stream()
                .filter(i -> !i.getIsPaid())
                .sorted(Comparator.comparing(LoanInstallmentEntity::getDueDate))
                .toList();
        if (loan.getIsPaid() || payableInstallments.isEmpty())
            throw new RuntimeException("There is no installment that can be paid");
        var paidInstallmentsCount = 0;
        var remainingAmount = amount;
        var totalSpentAmount = BigDecimal.ZERO;
        var now = LocalDate.now(clock);
        for (LoanInstallmentEntity installment : payableInstallments) {
            var paymentAmount = getShouldBePaidAmount(installment.getAmount(), installment.getDueDate());
            var maxAllowedDate = now.plusMonths(2);
            if (paymentAmount.compareTo(remainingAmount) > 0 || installment.getDueDate().isAfter(maxAllowedDate)) {
                break;
            }
            installment.pay(LocalDateTime.now(clock), paymentAmount);
            paidInstallmentsCount++;
            remainingAmount = remainingAmount.subtract(paymentAmount);
            totalSpentAmount = totalSpentAmount.add(paymentAmount);
        }

        var totalPaidInstallments = loan.getInstallments().stream().filter(LoanInstallmentEntity::getIsPaid).toList().size();
        if (totalPaidInstallments == loan.getNumberOfInstallments()) {
            loan.setAsPaid();
            var customer = loan.getCustomer();
            customer.decreaseUsedCreditLimit(loan.getLoanAmount());
            customerRepository.save(customer);
            log.info("loan has been completed successfully");
        }
        if (paidInstallmentsCount > 0){
            loanRepository.save(loan);
            log.info("installments have been paid successfully");
        }
        return new PayLoanResponse(paidInstallmentsCount, totalPaidInstallments, loan.getNumberOfInstallments() - totalPaidInstallments, totalSpentAmount, remainingAmount, loan.getIsPaid());
    }

    @Override
    public boolean isCustomerOwnerOfLoan(UUID loanId, UUID customerId) {
        var loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        return loan.getCustomer().getId().equals(customerId);
    }

    private BigDecimal getShouldBePaidAmount(BigDecimal amount, LocalDate dueDate) {
        var now = LocalDate.now(clock);
        var rewardRate = loanConfiguration.getPaymentRewardRate();
        var penaltyRate = loanConfiguration.getPaymentPenaltyRate();
        var days = ChronoUnit.DAYS.between(now, dueDate);
        if (days > 0) {
            var rewardedAmount = rewardRate.multiply(amount).multiply(BigDecimal.valueOf(days));
            return amount.subtract(rewardedAmount).max(BigDecimal.ZERO);
        } else if (days < 0) {
            var penaltyAmount = penaltyRate.multiply(amount).multiply(BigDecimal.valueOf(Math.abs(days)));
            return amount.add(penaltyAmount);
        } else {
            return amount;
        }
    }
}
