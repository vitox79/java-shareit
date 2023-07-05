package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBooking() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;

        Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("item1")
            .available(false)
            .build();
        User user = User.builder()
            .id(1L)
            .email("mail")
            .name("name")
            .build();

        BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(dateTime.plusHours(1))
            .end(dateTime.plusHours(2))
            .item(item)
            .booker(user)
            .status(Status.REJECTED.toString())
            .build();
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
            .isEqualTo(dateTime.plusHours(1)
                .truncatedTo(ChronoUnit.SECONDS)
                .toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
            .isEqualTo(dateTime.plusHours(2)
                .truncatedTo(ChronoUnit.SECONDS)
                .toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name")
            .isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
            .isEqualTo("item1");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
            .isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
            .isEqualTo("mail");
    }
}