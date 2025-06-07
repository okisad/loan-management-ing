package com.ing.credit.dao.entities;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ing.credit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_installments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LoanInstallmentEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private LoanEntity loan;

    private BigDecimal amount;

    private BigDecimal paidAmount;

    private LocalDate dueDate;

    private LocalDateTime paymentDate;

    private Boolean isPaid;

    static LoanInstallmentEntity createInstallment(LoanEntity loan,
                                                   BigDecimal amount,
                                                   LocalDate dueDate) {
        LoanInstallmentEntity loanInstallmentEntity = new LoanInstallmentEntity();
        loanInstallmentEntity.loan = loan;
        loanInstallmentEntity.amount = amount;
        loanInstallmentEntity.paidAmount = BigDecimal.ZERO;
        loanInstallmentEntity.dueDate = dueDate;
        loanInstallmentEntity.isPaid = false;
        return loanInstallmentEntity;
    }

    public void pay(LocalDateTime paymentDate, BigDecimal paidAmount) {
        this.isPaid = true;
        this.paidAmount = paidAmount;
        this.paymentDate = paymentDate;
    }


}
