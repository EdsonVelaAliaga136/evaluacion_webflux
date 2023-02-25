package com.mitocode.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Clase de seguridad numero 2
public class AuthRequest {
    private String username;
    private String password;
}
