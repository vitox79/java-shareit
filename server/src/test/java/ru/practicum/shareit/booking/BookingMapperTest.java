package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class BookingMapperTest {
    @Test
    public void testToBooking() {
        BookingDto bookingDto = BookingDto.builder()
            .start(LocalDateTime.of(2023, 7, 1, 10, 0))
            .end(LocalDateTime.of(2023, 7, 1, 12, 0))
            .build();

        Booking booking = BookingMapper.toBooking(bookingDto);

        Assertions.assertEquals(bookingDto.getStart(), booking.getStart());
        Assertions.assertEquals(bookingDto.getEnd(), booking.getEnd());
    }

    @Test
    public void testToBookingDto() {
        User booker = new User();
        Item item = new Item();
        Booking booking = Booking.builder().booker(booker).status(Status.WAITING).item(item).build();

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        Assertions.assertEquals(booking.getId(), bookingDto.getId());
        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(booking.getStatus().toString(), bookingDto.getStatus());
        Assertions.assertEquals(booking.getBooker(), bookingDto.getBooker());
        Assertions.assertEquals(booking.getItem(), bookingDto.getItem());
    }

    @Test
    public void testToBookingInfoDto() {
        User booker = new User();
        Booking booking = Booking.builder().booker(booker).build();

        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        Assertions.assertEquals(booking.getId(), bookingInfoDto.getId());
        Assertions.assertEquals(booking.getBooker().getId(), bookingInfoDto.getBookerId());
    }

    @Test
    public void testToBookingWithRequestDto() {
        User booker = new User();
        Item item = new Item();
        BookingRequestDto requestDto = BookingRequestDto.builder()
            .start(LocalDateTime.of(2023, 7, 1, 10, 0))
            .end(LocalDateTime.of(2023, 7, 1, 12, 0))
            .build();

        Booking booking = BookingMapper.toBooking(requestDto, item, booker);

        Assertions.assertEquals(requestDto.getStart(), booking.getStart());
        Assertions.assertEquals(requestDto.getEnd(), booking.getEnd());
        Assertions.assertEquals(Status.WAITING, booking.getStatus());
        Assertions.assertEquals(booker, booking.getBooker());
        Assertions.assertEquals(item, booking.getItem());
    }
}
