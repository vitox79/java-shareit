package ru.practicum.shareit.request;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}