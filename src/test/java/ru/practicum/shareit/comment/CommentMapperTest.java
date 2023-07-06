package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private final CommentMapper mapper = new CommentMapper();

    @Test
    void toComment() {
        CommentDto commentDto = CommentDto.builder().text("text").build();
        User user = User.builder().id(1L).name("user").email("mail").build();
        Item item = Item.builder().name("user").description("that").available(false).build();
        LocalDateTime time = LocalDateTime.now();
        Comment comment = mapper.toComment(user, item, commentDto, time);

        assertEquals(user, comment.getAuthor(), "Wrong user");
        assertEquals(item, comment.getItem(), "Wrong item");
        assertEquals(commentDto.getText(), comment.getText(), "wrong text");
        assertEquals(time, comment.getCreated(), "wrong created");
    }

    @Test
    void toCommentDto() {
        Comment comment = Comment.builder()
            .created(LocalDateTime.now())
            .text("text")
            .author(User.builder().id(1L).name("user").email("mail").build())
            .id(1L)
            .build();
        CommentDto commentDto = mapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId(), "wrong id");
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName(), "wrong user");
        assertEquals(comment.getText(), commentDto.getText(), "wrong text");
        assertEquals(comment.getCreated(), commentDto.getCreated(), "wrong created");
    }
}