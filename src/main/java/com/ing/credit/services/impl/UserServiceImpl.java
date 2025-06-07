package com.ing.credit.services.impl;

import com.ing.credit.config.JwtUtil;
import com.ing.credit.dao.entities.CustomerEntity;
import com.ing.credit.dao.entities.UserEntity;
import com.ing.credit.dao.repositories.CustomerRepository;
import com.ing.credit.dao.repositories.UserRepository;
import com.ing.credit.dtos.RoleEnum;
import com.ing.credit.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void createAdminUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already in use");
        }
        var encodedPassword = passwordEncoder.encode(password);
        UserEntity userEntity = UserEntity.createUserEntity(username, encodedPassword, RoleEnum.ADMIN.name());
        userRepository.save(userEntity);
    }

    @Override
    public String login(String username, String password) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }
        var customerId = customerRepository.findByUser_Id(user.getId()).map(CustomerEntity::getId).orElse(null);
        return jwtUtil.generateToken(username, user.getId(), customerId, user.getRoles());

    }
}
