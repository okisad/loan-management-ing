package com.ing.credit.common.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidInstallmentImpl.class)
public @interface ValidInstallment {

    String message() default "Invalid number of installments";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
