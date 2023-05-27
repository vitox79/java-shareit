package ru.practicum.shareit.request.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
}