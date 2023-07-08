package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.model.Comment;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    Comment comment = Comment.builder().id(1L).build();

    @Test
    void testEquals() {
        assertTrue(comment.equals(Comment.builder().id(1L).build()));
    }

    @Test
    void testHashCode() {
        Comment comment1 = Comment.builder().id(1L).build();
        assertEquals(comment1.hashCode(), comment.hashCode());
    }
}