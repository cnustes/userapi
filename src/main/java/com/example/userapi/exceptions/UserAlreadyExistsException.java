package com.example.userapi.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("El correo ya está registrado: " + email);
    }
}
