package com.siva.springmicroservices.dto;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String token;

    private String type;

    private Date expiresAt;

    private long expiresIn;
}
