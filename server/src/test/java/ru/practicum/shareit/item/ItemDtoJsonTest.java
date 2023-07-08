package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void itemDtoTest() throws Exception {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
        BookingInfoDto last = BookingInfoDto.builder()
            .bookerId(1L)
            .id(1L)
            .build();
        BookingInfoDto next = BookingInfoDto.builder()
            .bookerId(2L)
            .id(2L)
            .build();

        ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .owner(1L)
            .requestId(1L)
            .name("name")
            .description("description")
            .available(true)
            .comments(List.of(CommentDto.builder()
                    .text("text")
                    .authorName("name")
                    .id(1L)
                    .created(time)
                .build()))
            .lastBooking(last)
            .nextBooking(next)
            .build();
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.owner")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
            .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.name")
            .isEqualTo("name");

        assertThat(result).extractingJsonPathStringValue("$.description")
            .isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
            .isEqualTo(true);
        assertThat(result).extractingJsonPathArrayValue("$.comments")
            .hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
            .isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
            .isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
            .isEqualTo(time.truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
            .isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
            .isEqualTo(2);


    }

}
