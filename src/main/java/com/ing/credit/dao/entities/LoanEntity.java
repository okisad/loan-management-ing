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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LoanEntity extends BaseEntity {


    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    private BigDecimal loanAmount;

    private Integer numberOfInstallments;

    private Boolean isPaid;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanInstallmentEntity> installments = new ArrayList<>();

    public static LoanEntity createLoan(CustomerEntity customer,
                                        BigDecimal loanAmount,
                                        BigDecimal interestRate,
                                        Integer numberOfInstallments,
                                        LocalDate now) {

        var perAmountOfInstallment = loanAmount.multiply(BigDecimal.ONE.add(interestRate)).divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        LoanEntity loan = new LoanEntity();
        loan.customer = customer;
        loan.loanAmount = loanAmount;
        loan.numberOfInstallments = numberOfInstallments;
        loan.installments = new ArrayList<>();
        loan.isPaid = false;

        var dueDate = now.withDayOfMonth(1).plusMonths(1);
        for (int i = 0; i < numberOfInstallments; i++) {
            var loanInstallment = LoanInstallmentEntity.createInstallment(loan, perAmountOfInstallment, dueDate);
            loan.installments.add(loanInstallment);
            dueDate = dueDate.plusMonths(1);
        }
        return loan;
    }

    public void setAsPaid() {
        this.isPaid = Boolean.TRUE;
    }
}
