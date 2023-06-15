package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.model.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.getBooker().setId(userId);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(long userId, long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getBooker().getId() != userId) {
            throw new NotFoundException("You are not authorized to update this booking.");
        }

        booking.setStatus(approved != null && approved ? Status.APPROVED : Status.REJECTED);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getBooker().getId() != userId) {
            throw new NotFoundException("You are not authorized to access this booking.");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(long userId, String state) {
        List<Booking> bookings;
        if ("ALL".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByBookerId(userId);
        } else {
            Status bookingStatus = Status.valueOf(state.toUpperCase());
            bookings = bookingRepository.findAllByBookerIdAndStatus(userId, bookingStatus);
        }

        return bookings.stream()
            .map(bookingMapper::toBookingDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String state) {
        List<Booking> bookings;
        if ("ALL".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByOwnerId(ownerId);
        } else {
            Status bookingStatus = Status.valueOf(state.toUpperCase());
            bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, bookingStatus);
        }

        return bookings.stream()
            .map(bookingMapper::toBookingDto)
            .collect(Collectors.toList());
    }
}