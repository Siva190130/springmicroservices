package com.siva.springmicroservices.service;

import com.siva.springmicroservices.dto.UserRequest;
import com.siva.springmicroservices.dto.UserResponse;
import com.siva.springmicroservices.entity.Role;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);

    UserResponse getCurrentUser();

    UserResponse updateUserRole(Long id, Role role);
}
