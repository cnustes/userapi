package com.example.userapi.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneDTO {
    @NotBlank(message = "{phone.number.notblank}")
    private String number;

    @NotBlank(message = "{phone.citycode.notblank}")
    private String citycode;

    @NotBlank(message = "{phone.countrycode.notblank}")
    private String countrycode;
}
