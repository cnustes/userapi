package com.example.userapi.dtos;

import com.example.userapi.validators.ValidPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDTO {
    @NotBlank(message = "{user.name.notblank}")
    private String name;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotBlank(message = "{user.password.notblank}")
    @ValidPassword
    private String password;

    @NotEmpty(message = "{user.phones.notempty}")
    private List<@Valid PhoneDTO> phones;
}
