package com.ing.credit.services.impl;

import com.ing.credit.dao.entities.CustomerEntity;
import com.ing.credit.dao.entities.UserEntity;
import com.ing.credit.dao.repositories.CustomerRepository;
import com.ing.credit.dao.repositories.UserRepository;
import com.ing.credit.dtos.RoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    CustomerServiceImpl customerService;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Captor
    ArgumentCaptor<CustomerEntity> customerCaptor;
    @Captor
    ArgumentCaptor<UserEntity> userCaptor;


    @Test
    void create_customer_with_valid_request() {
        var username = "username";

        when(userRepository.existsByUsername(username)).thenReturn(Boolean.FALSE);

        var response = customerService.createCustomer("username", "pass1","firstname", "lastname", BigDecimal.valueOf(10000));

        verify(userRepository).save(userCaptor.capture());
        var user = userCaptor.getValue();

        verify(customerRepository).save(customerCaptor.capture());
        var customer = customerCaptor.getValue();

        assertEquals(username, user.getUsername());
        assertTrue(user.getRoles().contains(RoleEnum.CUSTOMER));

        assertEquals("firstname", customer.getFirstName());
        assertEquals("lastname", customer.getLastName());
        assertEquals(BigDecimal.valueOf(10000), customer.getCreditLimit());

        assertEquals(username, response.username());
        assertNotNull(response.password());
    }

    @Test
    void throw_exception_when_there_is_a_user_with_same_username() {
        var username = "username";

        when(userRepository.existsByUsername(username)).thenReturn(Boolean.TRUE);

        var runtimeException = assertThrows(RuntimeException.class, () -> customerService.createCustomer("username", "pass1","firstname", "lastname", BigDecimal.valueOf(10000)));
        assertEquals("Username is already in use", runtimeException.getMessage());
    }

    @Test
    void list_customers(){
        var firstId = UUID.randomUUID();
        var secondId = UUID.randomUUID();
        var thirdId = UUID.randomUUID();
        when(customerRepository.findAll()).thenReturn(List.of(
                new CustomerEntity(firstId, "a", "aa",BigDecimal.ONE,BigDecimal.ZERO, null),
                new CustomerEntity(secondId, "b", "ab",BigDecimal.TWO,BigDecimal.ZERO, null),
                new CustomerEntity(thirdId, "c", "ac",BigDecimal.TEN,BigDecimal.ZERO, null)
        ));

        var response = customerService.listCustomers();

        verify(customerRepository).findAll();

        assertEquals(firstId, response.getFirst().id());
        assertEquals("a", response.getFirst().firstName());
        assertEquals("aa", response.getFirst().lastName());
        assertEquals(BigDecimal.ONE, response.getFirst().creditLimit());
        assertEquals(BigDecimal.ZERO, response.getFirst().usedCredit());

        assertEquals(secondId, response.get(1).id());
        assertEquals("b", response.get(1).firstName());
        assertEquals("ab", response.get(1).lastName());
        assertEquals(BigDecimal.TWO, response.get(1).creditLimit());
        assertEquals(BigDecimal.ZERO, response.get(1).usedCredit());

        assertEquals(thirdId, response.get(2).id());
        assertEquals("c", response.get(2).firstName());
        assertEquals("ac", response.get(2).lastName());
        assertEquals(BigDecimal.TEN, response.get(2).creditLimit());
        assertEquals(BigDecimal.ZERO, response.get(2).usedCredit());
    }

}