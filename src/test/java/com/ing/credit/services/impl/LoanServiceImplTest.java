package com.ing.credit.services.impl;

import com.ing.credit.config.LoanConfiguration;
import com.ing.credit.dao.entities.CustomerEntity;
import com.ing.credit.dao.entities.LoanEntity;
import com.ing.credit.dao.entities.LoanInstallmentEntity;
import com.ing.credit.dao.repositories.CustomerRepository;
import com.ing.credit.dao.repositories.LoanRepository;
import com.ing.credit.dtos.responses.LoanResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {
    @InjectMocks
    LoanServiceImpl loanService;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    LoanRepository loanRepository;

    @Mock
    LoanConfiguration loanConfiguration;

    @Mock
    Clock clock;

    @Captor
    ArgumentCaptor<LoanEntity> loanCaptor;
    @Captor
    ArgumentCaptor<CustomerEntity> customerCaptor;


    @Test
    void creating_loan_successfully() {
        var customerId = UUID.randomUUID();

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.15);
        var numberOfInstallments = 12;

        var customerCreditLimit = BigDecimal.valueOf(30000);
        var customerUsedCreditLimit = BigDecimal.valueOf(10000);

        given(clock.instant()).willReturn(Instant.parse("2024-01-30T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        when(loanConfiguration.getAllowedMinimumInterestRate()).thenReturn(BigDecimal.valueOf(0.1));
        when(loanConfiguration.getAllowedMaximumInterestRate()).thenReturn(BigDecimal.valueOf(0.5));
        when(loanConfiguration.getAllowedInstallmentCounts()).thenReturn(List.of(3, 6, 9, 12));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new CustomerEntity(customerId, "first", "last", customerCreditLimit, customerUsedCreditLimit,null)));
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        loanService.createLoan(customerId, loanAmount, interestRate, numberOfInstallments);

        verify(loanRepository).save(loanCaptor.capture());
        LoanEntity capturedLoan = loanCaptor.getValue();

        verify(customerRepository).save(customerCaptor.capture());
        CustomerEntity capturedCustomer = customerCaptor.getValue();

        assertEquals(customerCreditLimit, capturedCustomer.getCreditLimit());
        assertEquals(customerUsedCreditLimit.add(loanAmount), capturedCustomer.getUsedCreditLimit());
        assertEquals(customerId, capturedLoan.getCustomer().getId());
        assertEquals(loanAmount, capturedLoan.getLoanAmount());
        assertEquals(numberOfInstallments, capturedLoan.getNumberOfInstallments());
        assertEquals(false, capturedLoan.getIsPaid());
        assertEquals(customerId, capturedLoan.getCustomer().getId());
        assertNotNull(capturedLoan.getInstallments());

        var createdInstallments = capturedLoan.getInstallments();

        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
        var dueDate = LocalDate.now(clock).withDayOfMonth(1).plusMonths(1);
        for (LoanInstallmentEntity installment : createdInstallments) {
            assertEquals(expectedInstallmentAmount, installment.getAmount());
            assertEquals(dueDate, installment.getDueDate());
            assertEquals(false, installment.getIsPaid());
            assertNull(installment.getPaymentDate());
            assertEquals(BigDecimal.ZERO, installment.getPaidAmount());
            dueDate = dueDate.plusMonths(1);
        }
    }

    @Test
    void throw_exception_insufficient_customer_limit_while_creating_loan() {
        var customerId = UUID.randomUUID();

        var loanAmount = BigDecimal.valueOf(30000);
        var interestRate = BigDecimal.valueOf(0.15);
        var numberOfInstallments = 12;

        var customerCreditLimit = BigDecimal.valueOf(30000);
        var customerUsedCreditLimit = BigDecimal.valueOf(10000);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new CustomerEntity(customerId, "first", "last", customerCreditLimit, customerUsedCreditLimit,null)));


        var runtimeExeption = assertThrows(RuntimeException.class, () -> loanService.createLoan(customerId, loanAmount, interestRate, numberOfInstallments));
        assertEquals("Customer not enough available credit limit", runtimeExeption.getMessage());
    }

    @Test
    void throw_exception_requesting_wrong_interest_rate_while_creating_loan() {
        var customerId = UUID.randomUUID();

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.015);
        var numberOfInstallments = 12;

        var customerCreditLimit = BigDecimal.valueOf(30000);
        var customerUsedCreditLimit = BigDecimal.valueOf(10000);

        when(loanConfiguration.getAllowedMinimumInterestRate()).thenReturn(BigDecimal.valueOf(0.1));
        when(loanConfiguration.getAllowedMaximumInterestRate()).thenReturn(BigDecimal.valueOf(0.5));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new CustomerEntity(customerId, "first", "last", customerCreditLimit, customerUsedCreditLimit,null)));


        var runtimeException = assertThrows(RuntimeException.class, () -> loanService.createLoan(customerId, loanAmount, interestRate, numberOfInstallments));
        assertEquals("Interest rate must be between 0.1 and 0.5", runtimeException.getMessage());
    }

    @Test
    void throw_exception_requesting_wrong_number_of_installments_while_creating_loan() {
        var customerId = UUID.randomUUID();

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.5);
        var numberOfInstallments = 17;

        var customerCreditLimit = BigDecimal.valueOf(30000);
        var customerUsedCreditLimit = BigDecimal.valueOf(10000);

        when(loanConfiguration.getAllowedInstallmentCounts()).thenReturn(List.of(3, 6, 9, 12));
        when(loanConfiguration.getAllowedMinimumInterestRate()).thenReturn(BigDecimal.valueOf(0.1));
        when(loanConfiguration.getAllowedMaximumInterestRate()).thenReturn(BigDecimal.valueOf(0.5));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new CustomerEntity(customerId, "first", "last", customerCreditLimit, customerUsedCreditLimit,null)));


        var runtimeException = assertThrows(RuntimeException.class, () -> loanService.createLoan(customerId, loanAmount, interestRate, numberOfInstallments));
        assertEquals("Installments must be one of 3,6,9,12", runtimeException.getMessage());
    }

    @Test
    void list_loans() {
        var customerId = UUID.randomUUID();
        var customer = new CustomerEntity(customerId, "first", "last", BigDecimal.valueOf(30000), BigDecimal.valueOf(18000),null);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        var firstLoanId = UUID.randomUUID();
        var secondLoanId = UUID.randomUUID();
        var thirdLoanId = UUID.randomUUID();

        LoanEntity loan1 = new LoanEntity(firstLoanId, customer, BigDecimal.valueOf(10000), 12, false, List.of());
        LoanEntity loan2 = new LoanEntity(secondLoanId, customer, BigDecimal.valueOf(5000), 6, true, List.of());
        LoanEntity loan3 = new LoanEntity(thirdLoanId, customer, BigDecimal.valueOf(3000), 9, false, List.of());

        var expectedLoans = List.of(loan1, loan2, loan3);
        when(loanRepository.findByCustomerId(customerId)).thenReturn(expectedLoans);

        var loans = loanService.listLoans(customerId);
        LoanResponse first = loans.getFirst();
        LoanResponse second = loans.get(1);
        LoanResponse third = loans.get(2);
        assertEquals(expectedLoans.size(), loans.size());
        assertEquals(firstLoanId, first.id());
        assertEquals(BigDecimal.valueOf(10000), first.loanAmount());
        assertEquals(12, first.numberOfInstallments());
        assertEquals(false, first.isPaid());

        assertEquals(secondLoanId, second.id());
        assertEquals(BigDecimal.valueOf(5000), second.loanAmount());
        assertEquals(6, second.numberOfInstallments());
        assertEquals(true, second.isPaid());

        assertEquals(thirdLoanId, third.id());
        assertEquals(BigDecimal.valueOf(3000), third.loanAmount());
        assertEquals(9, third.numberOfInstallments());
        assertEquals(false, third.isPaid());

    }

    @Test
    void throw_exception_while_paying_loan_when_wrong_loan_id() {
        UUID loanId = UUID.randomUUID();

        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        var runtimeException = assertThrows(RuntimeException.class, () -> loanService.payLoan(loanId, BigDecimal.valueOf(10000)));
        assertEquals("Loan not found", runtimeException.getMessage());
    }

    @Test
    void throw_exception_while_paying_loan_when_there_is_no_payable_installment() {
        UUID loanId = UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.parse("2024-01-30T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));
        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.1);
        var numberOfInstallments = 3;
        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        var installments = generateInstallments(loanAmount, interestRate, 3, 3);
        var loan = new LoanEntity(loanId, null, loanAmount, numberOfInstallments, true, installments);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        var runtimeException = assertThrows(RuntimeException.class, () -> loanService.payLoan(loanId, BigDecimal.valueOf(10000)));
        assertEquals("There is no installment that can be paid", runtimeException.getMessage());
    }

    @Test
    void try_to_pay_loan_with_less_amount_of_following_installments_then_installment_should_not_be_paid() {
        UUID loanId = UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.parse("2024-01-30T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        when(loanConfiguration.getPaymentRewardRate()).thenReturn(BigDecimal.valueOf(0.001));
        when(loanConfiguration.getPaymentPenaltyRate()).thenReturn(BigDecimal.valueOf(0.001));

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.1);
        var numberOfInstallments = 3;
        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        var installments = generateInstallments(loanAmount, interestRate, 3, 1);
        var customer = new CustomerEntity(UUID.randomUUID(),"first","last", BigDecimal.valueOf(30000), BigDecimal.valueOf(10000),null);
        var loan = new LoanEntity(loanId, customer, loanAmount, numberOfInstallments, false, installments);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        var paymentResponse = loanService.payLoan(loanId, expectedInstallmentAmount.subtract(BigDecimal.valueOf(100)));

        assertEquals(0 , paymentResponse.numberOfPaidInstallments());
        assertEquals(1 , paymentResponse.totalNumberOfPaidInstallments());
        assertEquals(2 , paymentResponse.numberOfRemainingInstallments());
        assertEquals(BigDecimal.ZERO, paymentResponse.spentAmount());
        assertEquals(expectedInstallmentAmount.subtract(BigDecimal.valueOf(100)) , paymentResponse.remainingAmount());
        assertFalse(paymentResponse.isLoanCompleted());
    }

    @Test
    void pay_just_one_installments_with_2_days_reward_but_loan_will_not_be_completed() {
        UUID loanId = UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.parse("2024-01-30T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        BigDecimal rewardedRate = BigDecimal.valueOf(0.001);
        BigDecimal penaltyRate = BigDecimal.valueOf(0.001);
        when(loanConfiguration.getPaymentRewardRate()).thenReturn(rewardedRate);
        when(loanConfiguration.getPaymentPenaltyRate()).thenReturn(penaltyRate);

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.1);
        var numberOfInstallments = 6;
        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        var installments = generateInstallments(loanAmount, interestRate, numberOfInstallments, 1);
        var customer = new CustomerEntity(UUID.randomUUID(),"first","last", BigDecimal.valueOf(30000), BigDecimal.valueOf(10000),null);
        var loan = new LoanEntity(loanId, customer, loanAmount, numberOfInstallments, false, installments);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        var paymentResponse = loanService.payLoan(loanId, expectedInstallmentAmount);

        var expectedPaymentAmount = expectedInstallmentAmount.subtract(expectedInstallmentAmount.multiply(BigDecimal.TWO).multiply(rewardedRate));

        assertEquals(1 , paymentResponse.numberOfPaidInstallments());
        assertEquals(2 , paymentResponse.totalNumberOfPaidInstallments());
        assertEquals(4 , paymentResponse.numberOfRemainingInstallments());
        assertEquals(expectedPaymentAmount, paymentResponse.spentAmount());
        assertEquals(expectedInstallmentAmount.subtract(expectedPaymentAmount) , paymentResponse.remainingAmount());
        assertFalse(paymentResponse.isLoanCompleted());
    }

    @Test
    void pay_just_two_installments_with_reward_and_penalty_but_loan_will_not_be_completed() {
        UUID loanId = UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.parse("2024-01-30T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        var rewardedRate = BigDecimal.valueOf(0.001);
        var penaltyRate = BigDecimal.valueOf(0.001);
        when(loanConfiguration.getPaymentRewardRate()).thenReturn(rewardedRate);
        when(loanConfiguration.getPaymentPenaltyRate()).thenReturn(penaltyRate);

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.1);
        var numberOfInstallments = 6;
        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        var installments = generateInstallments(loanAmount, interestRate, numberOfInstallments, 1);
        var customer = new CustomerEntity(UUID.randomUUID(),"first","last", BigDecimal.valueOf(30000), BigDecimal.valueOf(10000),null);
        var loan = new LoanEntity(loanId, customer, loanAmount, numberOfInstallments, false, installments);


        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        given(clock.instant()).willReturn(Instant.parse("2024-02-05T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        var paymentResponse = loanService.payLoan(loanId, expectedInstallmentAmount.multiply(BigDecimal.valueOf(2.5)));

        var firstExpectedPaymentAmount = expectedInstallmentAmount.add(expectedInstallmentAmount.multiply(BigDecimal.valueOf(4)).multiply(rewardedRate));
        var secondExpectedPaymentAmount = expectedInstallmentAmount.subtract(expectedInstallmentAmount.multiply(BigDecimal.valueOf(25)).multiply(rewardedRate));
        var totalExpectedPaymentAmount = firstExpectedPaymentAmount.add(secondExpectedPaymentAmount);

        assertEquals(2 , paymentResponse.numberOfPaidInstallments());
        assertEquals(3 , paymentResponse.totalNumberOfPaidInstallments());
        assertEquals(3 , paymentResponse.numberOfRemainingInstallments());
        assertEquals(totalExpectedPaymentAmount, paymentResponse.spentAmount());
        assertEquals(expectedInstallmentAmount.multiply(BigDecimal.valueOf(2.5)).subtract(totalExpectedPaymentAmount) , paymentResponse.remainingAmount());
        assertFalse(paymentResponse.isLoanCompleted());
    }

    @Test
    void pay_just_last_installment_and_loan_will_be_completed() {
        UUID loanId = UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.parse("2024-01-30T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        var rewardedRate = BigDecimal.valueOf(0.001);
        var penaltyRate = BigDecimal.valueOf(0.001);
        when(loanConfiguration.getPaymentRewardRate()).thenReturn(rewardedRate);
        when(loanConfiguration.getPaymentPenaltyRate()).thenReturn(penaltyRate);

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.1);
        var numberOfInstallments = 6;
        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        var installments = generateInstallments(loanAmount, interestRate, numberOfInstallments, 5);
        var customer = new CustomerEntity(UUID.randomUUID(),"first","last", BigDecimal.valueOf(30000), BigDecimal.valueOf(10000),null);
        var loan = new LoanEntity(loanId, customer, loanAmount, numberOfInstallments, false, installments);


        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        given(clock.instant()).willReturn(Instant.parse("2024-02-01T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        var paymentResponse = loanService.payLoan(loanId, expectedInstallmentAmount);


        assertEquals(1 , paymentResponse.numberOfPaidInstallments());
        assertEquals(6 , paymentResponse.totalNumberOfPaidInstallments());
        assertEquals(0 , paymentResponse.numberOfRemainingInstallments());
        assertEquals(expectedInstallmentAmount, paymentResponse.spentAmount());
        assertEquals(0, paymentResponse.remainingAmount().compareTo(BigDecimal.ZERO));
        assertTrue(paymentResponse.isLoanCompleted());
    }

    @Test
    void pay_just_following_three_months() {
        UUID loanId = UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.parse("2023-12-01T12:15:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UTC"));

        var rewardedRate = BigDecimal.valueOf(0.001);
        var penaltyRate = BigDecimal.valueOf(0.001);
        when(loanConfiguration.getPaymentRewardRate()).thenReturn(rewardedRate);
        when(loanConfiguration.getPaymentPenaltyRate()).thenReturn(penaltyRate);

        var loanAmount = BigDecimal.valueOf(10000);
        var interestRate = BigDecimal.valueOf(0.1);
        var numberOfInstallments = 6;
        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        var installments = generateInstallments(loanAmount, interestRate, numberOfInstallments, 0);
        var customer = new CustomerEntity(UUID.randomUUID(),"first","last", BigDecimal.valueOf(30000), BigDecimal.valueOf(10000),null);
        var loan = new LoanEntity(loanId, customer, loanAmount, numberOfInstallments, false, installments);


        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        given(clock.instant()).willReturn(Instant.parse("2024-01-01T12:15:00Z"));

        var paymentResponse1 = loanService.payLoan(loanId, expectedInstallmentAmount.add(BigDecimal.valueOf(100)));
        assertEquals(1 , paymentResponse1.numberOfPaidInstallments());

        var paymentResponse2 = loanService.payLoan(loanId, expectedInstallmentAmount);
        assertEquals(1 , paymentResponse2.numberOfPaidInstallments());

        var paymentResponse3 = loanService.payLoan(loanId, expectedInstallmentAmount);
        assertEquals(1 , paymentResponse3.numberOfPaidInstallments());

        var notPaidPaymentResponse = loanService.payLoan(loanId, expectedInstallmentAmount);

        assertEquals(0 , notPaidPaymentResponse.numberOfPaidInstallments());
        assertEquals(3 , notPaidPaymentResponse.totalNumberOfPaidInstallments());
        assertEquals(3 , notPaidPaymentResponse.numberOfRemainingInstallments());
        assertFalse(notPaidPaymentResponse.isLoanCompleted());

        given(clock.instant()).willReturn(Instant.parse("2024-02-01T12:15:00Z"));

        var paymentAfterOneMonthResponse = loanService.payLoan(loanId, expectedInstallmentAmount);

        assertEquals(1 , paymentAfterOneMonthResponse.numberOfPaidInstallments());
        assertEquals(4 , paymentAfterOneMonthResponse.totalNumberOfPaidInstallments());
        assertEquals(2 , paymentAfterOneMonthResponse.numberOfRemainingInstallments());
        assertFalse(paymentAfterOneMonthResponse.isLoanCompleted());
    }


    List<LoanInstallmentEntity> generateInstallments(BigDecimal loanAmount, BigDecimal interestRate, int numberOfInstallments, int paidInstallmentCount) {
        BigDecimal expectedInstallmentAmount = loanAmount
                .add(loanAmount.multiply(interestRate))
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
        var installments = new ArrayList<LoanInstallmentEntity>();
        var dueDate = LocalDate.now(clock).withDayOfMonth(1).plusMonths(1).minusMonths(paidInstallmentCount);
        for (int i = 0; i < numberOfInstallments; i++) {
            var isPaid = paidInstallmentCount - i > 0;
            installments.add(new LoanInstallmentEntity(UUID.randomUUID(),
                    null,
                    expectedInstallmentAmount,
                    isPaid ? expectedInstallmentAmount : BigDecimal.ZERO,
                    dueDate,
                    isPaid ? LocalDateTime.now() : null,
                    isPaid));
            dueDate = dueDate.plusMonths(1);
        }
        return installments;
    }
}