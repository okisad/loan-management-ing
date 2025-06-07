package com.ing.credit.services;


import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {

    void createAdminUser(@NotNull String username, @NotNull String password);

    String login(@NotNull String username,@NotNull  String password);

}
