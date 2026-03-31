package com.javarush.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно содержать от 2-100 символов")
    private String name;

    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

}
