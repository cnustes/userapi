package com.example.userapi.services;

import com.example.userapi.dtos.LoginRequestDTO;
import com.example.userapi.dtos.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
}