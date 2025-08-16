package com.example.userapi.dtos;

import lombok.Data;

@Data
public class PhoneResponseDTO {
    private String number;
    private String citycode;
    private String countrycode;
}
