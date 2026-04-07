package com.javarush.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Статус ответа 201 Created
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    // id
    private Long id;
    // name
    private String name;
    // email
    private String email;

}
