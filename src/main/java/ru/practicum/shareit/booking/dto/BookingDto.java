package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class BookingDto {
    Long id;
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
    Item item;
    Long itemId;
    User booker;
    String status;
}