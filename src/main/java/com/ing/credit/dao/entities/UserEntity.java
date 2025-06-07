package com.ing.credit.dao.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ing.credit.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

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

    private String roles;


    public static UserEntity createUserEntity(String username, String encodedPassword, String roles) {
        var userEntity = new UserEntity();
        userEntity.username = username;
        userEntity.password = encodedPassword;
        userEntity.roles = roles;
        return userEntity;

    }
}
