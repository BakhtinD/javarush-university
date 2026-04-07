package com.javarush.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserDto {

    // name
    @NotBlank(message = "Name is required")
    private String name;
    // email
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

}
