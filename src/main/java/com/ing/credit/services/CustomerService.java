package com.ing.credit.services;

import com.ing.credit.dtos.responses.CreateCustomerResponse;
import com.ing.credit.dtos.responses.CustomerResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    CreateCustomerResponse createCustomer(String username, String password, String firstName, String lastName, BigDecimal creditLimit);

    List<CustomerResponse> listCustomers();

}
