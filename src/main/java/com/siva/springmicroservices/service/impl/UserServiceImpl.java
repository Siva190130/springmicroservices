package com.siva.springmicroservices.service.impl;

import com.siva.springmicroservices.dto.UserRequest;
import com.siva.springmicroservices.dto.UserResponse;
import com.siva.springmicroservices.entity.User;
import com.siva.springmicroservices.exception.EmailAlreadyExistsException;
import com.siva.springmicroservices.exception.UserNotFoundException;
import com.siva.springmicroservices.repo.UserRepository;
import com.siva.springmicroservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException(
                    "Email already exists");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(
                        request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();
        userRepo.save(user);
        return mapToResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {

        return userRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(Long id) {

        User user= userRepo.findById(id).
                orElseThrow(()->
                        new UserNotFoundException("User not Found with id"+ id));
        return mapToResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(Long id, UserRequest request) {

        User existingUser =
                userRepo.findByEmail(request.getEmail())
                        .orElse(null);

        if(existingUser != null &&
                !existingUser.getId().equals(id)) {

            throw new EmailAlreadyExistsException(
                    "Email already exists");
        }

        User user= userRepo.findById(id).
                orElseThrow(()->
                        new UserNotFoundException("User not Found with id"+ id));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());

        userRepo.save(user);

        return mapToResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {

        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id " + id));

        userRepo.delete(user);

    }

    private UserResponse mapToResponse(User user){

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + email));

        return mapToResponse(user);
    }
}
