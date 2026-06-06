package com.siva.springmicroservices.dto;

import com.siva.springmicroservices.entity.Role;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    private Role role;
}
