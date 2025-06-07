package com.ing.credit.dao.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ing.credit.common.BaseEntity;
import com.ing.credit.dtos.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private List<RoleEnum> roles;


    public static UserEntity createUserEntity(String username, String encodedPassword, List<RoleEnum> roles) {
        var userEntity = new UserEntity();
        userEntity.username = username;
        userEntity.password = encodedPassword;
        userEntity.roles = roles;
        return userEntity;

    }
}
