package com.mitocode.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Clase de seguridad numero 3
public class AuthResponse {
    private String token;
    private Date expiration;
}
