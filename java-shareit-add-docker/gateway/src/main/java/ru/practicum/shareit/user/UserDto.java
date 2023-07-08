package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;

    @NotBlank(message = "Name must not be empty.", groups = Create.class)
    @Size(max = 255, groups = {Create.class, Update.class})
    String name;

    @NotBlank(message = "Email must not be empty.", groups = Create.class)
    @Email(message = "Invalid email format.", groups = {Create.class, Update.class})
    @Size(max = 512, groups = {Create.class, Update.class})
    String email;
}