package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "status", source = "booking.status")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "status", source = "bookingDto.status")
    Booking toBooking(BookingDto bookingDto);
}