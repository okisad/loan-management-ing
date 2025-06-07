package com.ing.credit.services.impl;

import com.ing.credit.dao.entities.LoanEntity;
import com.ing.credit.dao.entities.LoanInstallmentEntity;
import com.ing.credit.dao.repositories.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanInstallmentServiceImplTest {
    @InjectMocks
    LoanInstallmentServiceImpl loanInstallmentService;
    @Mock
    LoanRepository loanRepository;

    @Test
    void list_installments() {
        var loanId = UUID.randomUUID();
        var firstInstallmentId = UUID.randomUUID();
        var secondInstallmentId = UUID.randomUUID();
        var thirdInstallmentId = UUID.randomUUID();

        when(loanRepository
                .findWithInstallmentsById(loanId))
                .thenReturn(
                        Optional.of(
                                new LoanEntity(loanId,
                                        null,
                                        BigDecimal.valueOf(12000),
                                        3,
                                        Boolean.FALSE,
                                        List.of(
                                                new LoanInstallmentEntity(firstInstallmentId, null, BigDecimal.valueOf(4000), BigDecimal.ZERO, LocalDate.of(2025, 1, 1), null, Boolean.FALSE),
                                                new LoanInstallmentEntity(secondInstallmentId, null, BigDecimal.valueOf(4000), BigDecimal.ZERO, LocalDate.of(2025, 2, 1), null, Boolean.FALSE),
                                                new LoanInstallmentEntity(thirdInstallmentId, null, BigDecimal.valueOf(4000), BigDecimal.ZERO, LocalDate.of(2025, 3, 1), null, Boolean.FALSE)
                                        ))
                        )
                );

        var response = loanInstallmentService.listInstallments(loanId);

        assertEquals(firstInstallmentId, response.get(0).id());
        assertEquals(LocalDate.of(2025, 1, 1), response.get(0).dueDate());
        assertEquals(Boolean.FALSE, response.get(0).isPaid());
        assertEquals(BigDecimal.valueOf(4000), response.get(0).amount());
        assertEquals(BigDecimal.ZERO, response.get(0).paidAmount());
        assertNull(response.get(0).paymentDate());

        assertEquals(secondInstallmentId, response.get(1).id());
        assertEquals(LocalDate.of(2025, 2, 1), response.get(1).dueDate());
        assertEquals(Boolean.FALSE, response.get(1).isPaid());
        assertEquals(BigDecimal.valueOf(4000), response.get(1).amount());
        assertEquals(BigDecimal.ZERO, response.get(1).paidAmount());
        assertNull(response.get(1).paymentDate());

        assertEquals(thirdInstallmentId, response.get(2).id());
        assertEquals(LocalDate.of(2025, 3, 1), response.get(2).dueDate());
        assertEquals(Boolean.FALSE, response.get(2).isPaid());
        assertEquals(BigDecimal.valueOf(4000), response.get(2).amount());
        assertEquals(BigDecimal.ZERO, response.get(2).paidAmount());
        assertNull(response.get(2).paymentDate());
    }

}