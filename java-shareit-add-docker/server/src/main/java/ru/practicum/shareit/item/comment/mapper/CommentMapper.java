package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(User user, Item item, CommentDto commentDto, LocalDateTime time) {
        return Comment.builder()
            .author(user)
            .item(item)
            .text(commentDto.getText())
            .created(time)
            .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .authorName(comment.getAuthor().getName())
            .text(comment.getText())
            .created(comment.getCreated())
            .build();
    }
}
