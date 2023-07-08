package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    User owner;

    String description;

    @Column(name = "created_date")
    LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemRequest)) return false;
        ItemRequest request = (ItemRequest) o;
        return id == request.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
