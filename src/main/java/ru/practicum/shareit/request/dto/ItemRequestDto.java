package ru.practicum.shareit.request.dto;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

public class ItemRequestDto {
    long id;
    User user;
    String description;
    LocalDate created;
}
