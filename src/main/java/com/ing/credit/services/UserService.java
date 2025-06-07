package com.ing.credit.services;


public interface UserService {

    void createAdminUser(String username, String password);

    String  login(String username, String password);

}
