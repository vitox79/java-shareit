package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;


@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long ownerId;
    private ItemRequest request;
}