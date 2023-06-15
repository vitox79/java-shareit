package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    private BookingMapper bookingMapper;

    public Booking createBooking(long userId, BookingDto bookingDto) {
        return bookingRepository.save(bookingMapper.toBooking(bookingDto));
    }

    public Booking updateBookingStatus(Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (approved) {
            booking.setStatus(Booking.Status.APPROVED);
        } else {
            booking.setStatus(Booking.Status.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    public Booking getBookingDetails(Long bookingId) {
        return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }

    public List<Booking> getUserBookings(Booking.Status state) {
        // If state is null, retrieve all bookings
        if (state == null) {
            return bookingRepository.findAllByBookerId(getCurrentUserId());
        }

        return bookingRepository.findAllByBookerIdAndStatus(getCurrentUserId(), state);
    }

    public List<Booking> getOwnerBookings(Booking.Status state) {
        // If state is null, retrieve all bookings
        if (state == null) {
            return bookingRepository.findAllByOwnerId(getCurrentUserId());
        }

        return bookingRepository.findAllByOwnerIdAndStatus(getCurrentUserId(), state);
    }

    // Helper method to get the current user's ID
    private Long getCurrentUserId() {
        // Implement the logic to retrieve the current user's ID
        // This can vary based on your authentication and session management approach
        // Replace this with your actual
    }
}