package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingRequestDto bookingDto);

    BookingDto update(long userId, long bookingId, Boolean approved);

    BookingDto getById(long userId, long bookingId);

    List<BookingDto> getAllByUser(long userId, String state, int from, int size);

    List<BookingDto> getAllByOwner(long ownerId, String state, int from, int size);
}
