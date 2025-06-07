package com.ing.credit.config;

import java.util.UUID;

public class JwtTokenContext {
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    private static final ThreadLocal<UUID> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<UUID> customerIdHolder = new ThreadLocal<>();

    public static void clear() {
        usernameHolder.remove();
        userIdHolder.remove();
        customerIdHolder.remove();
    }

    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    public static String getUsername() {
        return usernameHolder.get();
    }

    public static void setUserId(UUID userId) {
        userIdHolder.set(userId);
    }

    public static UUID getUserId() {
        return userIdHolder.get();
    }

    public static void setCustomerId(UUID customerId) {
        customerIdHolder.set(customerId);
    }

    public static UUID getCustomerId() {
        return customerIdHolder.get();
    }
}
