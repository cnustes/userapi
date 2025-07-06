package com.example.userapi.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String token;
    private String created;
    private String modified;
    private String lastLogin;
    private boolean isActive;
    private List<PhoneResponseDTO> phones;
}
