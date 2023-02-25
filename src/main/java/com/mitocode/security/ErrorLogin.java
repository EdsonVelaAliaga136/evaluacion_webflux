package com.mitocode.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Clase de seguridad numero 8
public class ErrorLogin {
    private String message;
    private Date timestamp;
}
