package com.ing.credit.services.impl;

import com.ing.credit.dao.entities.CustomerEntity;
import com.ing.credit.dao.entities.UserEntity;
import com.ing.credit.dao.repositories.CustomerRepository;
import com.ing.credit.dao.repositories.UserRepository;
import com.ing.credit.dtos.RoleEnum;
import com.ing.credit.dtos.responses.CreateCustomerResponse;
import com.ing.credit.dtos.responses.CustomerResponse;
import com.ing.credit.services.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public CreateCustomerResponse createCustomer(String username, String password, String firstName, String lastName, BigDecimal creditLimit) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already in use");
        }
        var user = UserEntity.createUserEntity(username, passwordEncoder.encode(password), List.of(RoleEnum.CUSTOMER));
        var savedUser = userRepository.save(user);
        var customer = CustomerEntity.create(firstName, lastName, creditLimit, savedUser);
        customerRepository.save(customer);
        log.info("Customer has been created with username {}", username);
        return new CreateCustomerResponse(username, password);
    }

    @Override
    public List<CustomerResponse> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(c -> new CustomerResponse(c.getId(), c.getFirstName(),c.getLastName(),c.getCreditLimit(), c.getUsedCreditLimit()))
                .toList();
    }
}
