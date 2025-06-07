package com.ing.credit;

import com.ing.credit.dao.repositories.CustomerRepository;
import com.ing.credit.services.CustomerService;
import com.ing.credit.services.LoanService;
import com.ing.credit.services.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;

@SpringBootApplication
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@OpenAPIDefinition(
        info = @Info(title = "Loan API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth")
)
public class CreditApplication {
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;


    public static void main(String[] args) {
        SpringApplication.run(CreditApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {

        userService.createAdminUser("oktay1","123123");

        customerService.createCustomer("customer1","pass1","a","a", BigDecimal.valueOf(10000));
        customerService.createCustomer("customer2","pass1","a","a", BigDecimal.valueOf(10000));
        customerService.createCustomer("customer3","pass1","a","a", BigDecimal.valueOf(10000));
    }

}
