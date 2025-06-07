package com.ing.credit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "loan")
@Data
public class LoanConfiguration {

    private List<Integer> allowedInstallmentCounts;

    private BigDecimal allowedMinimumInterestRate;
    private BigDecimal allowedMaximumInterestRate;

    private BigDecimal paymentRewardRate;
    private BigDecimal paymentPenaltyRate;
}
