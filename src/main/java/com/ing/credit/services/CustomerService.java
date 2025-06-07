package com.ing.credit.services;

import com.ing.credit.dtos.responses.CreateCustomerResponse;
import com.ing.credit.dtos.responses.CustomerResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

@Validated
public interface CustomerService {

    CreateCustomerResponse createCustomer(@NotNull String username,
                                          @NotNull String password,
                                          @NotNull String firstName,
                                          @NotNull String lastName,
                                          @NotNull BigDecimal creditLimit);

    List<CustomerResponse> listCustomers();

}
