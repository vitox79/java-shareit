package ru.practicum.shareit.item.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Table;

@Table(name = "itemsRequest")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ItemRequest {
    Long itemId;
    String start;
    String end;
}
