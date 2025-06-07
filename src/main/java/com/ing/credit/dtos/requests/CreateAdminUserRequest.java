package com.ing.credit.dtos.requests;

public record CreateAdminUserRequest(
        String username,
        String password
) {
}
