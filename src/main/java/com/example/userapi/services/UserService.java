package com.example.userapi.services;

import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.dtos.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO request);
}
