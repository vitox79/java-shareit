package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toInfoBookingDto() {
        Booking booking = Booking.builder()
            .id(1L)
            .booker(User.builder().id(1L).name("name").email("mail").build())
            .build();
        BookingInfoDto itemsBookingDto = BookingMapper.toBookingInfoDto(booking);

        assertEquals(booking.getId(), itemsBookingDto.getId(), "id in InfoDto");
        assertEquals(booking.getBooker().getId(), itemsBookingDto.getBookerId(), "user in InfoDto");
    }

    @Test
    void toBooking() {
        BookingDto bookingDto = BookingDto.builder().start(LocalDateTime.now()).end(LocalDateTime.now()).build();
        Booking booking = BookingMapper.toBooking(bookingDto);

        assertEquals(bookingDto.getStart(), booking.getStart(), "start in booking ");
        assertEquals(bookingDto.getEnd(), booking.getEnd(), "end  in booking");

    }
    @Test
    void toBookingDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(Status.WAITING)
                .item(Item.builder().available(true).description("that").name("name").build())
                .booker(User.builder().id(1L).name("name").email("mail").build())
                .build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getEnd(), bookingDto.getEnd(), "end in dto");
        assertEquals(booking.getStart(), bookingDto.getStart(), "start in dto ");
        assertEquals(booking.getId(), bookingDto.getId(), "id in dto");
        assertEquals(booking.getStatus().toString(), bookingDto.getStatus(), "status in dto");
        assertEquals(booking.getBooker(), bookingDto.getBooker(), "booker in dto");
        assertEquals(booking.getItem(), bookingDto.getItem(), "item in dto");
    }


}