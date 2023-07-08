package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void commentDtoTest() throws IOException {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;

        CommentDto commentDto = CommentDto.builder()
            .text("text")
            .id(1L)
            .created(time)
            .authorName("name")
            .build();
        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
            .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.created")
            .isEqualTo(time
                .truncatedTo(ChronoUnit.SECONDS)
                .toString());

        assertThat(result).extractingJsonPathStringValue("$.text")
            .isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName")
            .isEqualTo("name");
    }
}
