package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotEmpty(message = "Invalid name")
    private String name;
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Empty email")
    private String email;

}