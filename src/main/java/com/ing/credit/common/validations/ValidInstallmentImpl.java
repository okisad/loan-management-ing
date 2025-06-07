package com.ing.credit.common.validations;

import com.ing.credit.config.LoanConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidInstallmentImpl implements ConstraintValidator<ValidInstallment, Integer> {

    private final LoanConfiguration loanConfiguration;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && loanConfiguration.getAllowedInstallmentCounts().contains(value);
    }
}
