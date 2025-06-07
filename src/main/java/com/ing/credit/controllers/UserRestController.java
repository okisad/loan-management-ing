package com.ing.credit.controllers;

import com.ing.credit.dtos.requests.LoginRequest;
import com.ing.credit.dtos.requests.CreateAdminUserRequest;
import com.ing.credit.dtos.responses.AccessTokenResponse;
import com.ing.credit.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
@Tag(name = "User Rest Controller")
@Slf4j
public class UserRestController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Admin User (ROLE: ADMIN)")
    @PostMapping("/users")
    public ResponseEntity<Void> createAdminUser(@RequestBody @Valid CreateAdminUserRequest adminUserRequest) {
        userService.createAdminUser(adminUserRequest.username(), adminUserRequest.password());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Access Token User")
    @PostMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> getJwtToken(@RequestBody @Valid LoginRequest loginRequest) {
        var jwtToken = userService.login(loginRequest.username(), loginRequest.password());
        return ResponseEntity.ok(new AccessTokenResponse(jwtToken));
    }

}
