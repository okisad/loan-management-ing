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
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CustomerEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String firstName;

    private String lastName;

    private BigDecimal creditLimit;

    private BigDecimal usedCreditLimit;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public static CustomerEntity create(
            String firstName,
            String lastName,
            BigDecimal creditLimit,
            UserEntity user
    ){
        var customerEntity = new CustomerEntity();
        customerEntity.creditLimit = creditLimit;
        customerEntity.firstName = firstName;
        customerEntity.lastName = lastName;
        customerEntity.usedCreditLimit = BigDecimal.ZERO;
        customerEntity.user = user;
        return customerEntity;
    }

    public void decreaseUsedCreditLimit(BigDecimal amount){
        this.usedCreditLimit = this.usedCreditLimit.subtract(amount);
    }

    public void increaseUsedCreditLimit(BigDecimal amount){
        this.usedCreditLimit = this.usedCreditLimit.add(amount);
    }

    public BigDecimal getAvailableCreditLimit(){
        return this.creditLimit.subtract(this.usedCreditLimit);
    }
}
