package ru.practicum.shareit.booking.mapper;


import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
            .end(bookingDto.getEnd())
            .start(bookingDto.getStart())
            .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
            .id(booking.getId())
            .end(booking.getEnd())
            .start(booking.getStart())
            .status(booking.getStatus().toString())
            .booker(booking.getBooker())
            .item(booking.getItem())
            .build();
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return BookingInfoDto.builder()
            .id(booking.getId())
            .bookerId(booking.getBooker().getId())
            .build();
    }

    public static Booking toBooking(BookingRequestDto requestDto, Item item, User booker) {
        return Booking.builder()
            .end(requestDto.getEnd())
            .start(requestDto.getStart())
            .status(Status.WAITING)
            .booker(booker)
            .item(item)
            .build();
    }


}

