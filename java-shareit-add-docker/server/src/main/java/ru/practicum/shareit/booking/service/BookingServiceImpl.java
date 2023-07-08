package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final RepositoryUser repositoryUser;

    private final ItemRepository repositoryItem;

    @Override
    public BookingDto create(long userId, BookingRequestDto bookingDto) {
        Optional<User> user = repositoryUser.findById(userId);
        Optional<Item> item = repositoryItem.findById(bookingDto.getItemId());

        if (user.isEmpty()) {
            throw new DataNotFoundException("user not found");
        }
        if (item.isEmpty()) {
            throw new DataNotFoundException("item not found");
        }
        if (!item.get().isAvailable()) {
            throw new NotFoundException("Item is not available");
        }

        timeValidation(bookingDto.getStart(), bookingDto.getEnd());
        Booking booking = BookingMapper.toBooking(bookingDto, item.get(), user.get());
        if (booking.getItem().getOwner().getId() == userId) {
            throw new DataNotFoundException("Booking is not available. You are owner.");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));


    }

    @Override
    public BookingDto update(long userId, long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
        if (approved == null) {
            throw new NotFoundException("approved is null");
        }
        if (booking.getItem().getOwner().getId() == userId) {
            if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
                throw new NotFoundException("Booking action already performed.");
            }
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else {
            throw new DataNotFoundException("You are not authorized to update this booking.");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Optional<User> user = repositoryUser.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("user not found");
        }
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new DataNotFoundException("Booking not found with id: " + bookingId));

        if ((booking.getItem().getOwner().getId() != userId) && (booking.getBooker().getId() != userId)) {
            throw new DataNotFoundException("You are not authorized to view this booking.");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(long userId, String stateString, int from, int size) {
        State state = State.fromString(stateString);

        Optional<User> user = repositoryUser.findById(userId);
        if (size <= 0) {
            throw new IllegalArgumentException("wrong size ");
        }
        int pageCount = (from + size - 1) / size;

        if (user.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }

        Page<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId,
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                bookings =
                    bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now,
                        PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case PAST:
                bookings =
                    bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                        PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStatusInOrderByStartDesc(userId,
                    Set.of(Status.WAITING, Status.APPROVED),
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING,
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED,
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            default:
                bookings = new PageImpl<>(Collections.emptyList());
                break;
        }

        return bookings.stream()
            .map(BookingMapper::toBookingDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String stateString, int from, int size) {
        State state = State.fromString(stateString);
        Optional<User> user = repositoryUser.findById(ownerId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("user not found");
        }

        int pageCount = (from + size - 1) / size;
        Page<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_OwnerIdOrderByStartDesc(ownerId,
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.REJECTED,
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case FUTURE:
                bookings = bookingRepository.findByOwnerIdAndStatusIn(ownerId, Set.of(Status.WAITING, Status.APPROVED),
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerIdPast(ownerId, LocalDateTime.now(),
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.WAITING,
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            case CURRENT:
                bookings = bookingRepository.findByOwnerIdCurrent(ownerId, LocalDateTime.now(),
                    PageRequest.of(pageCount, size, Sort.by("start").descending()));
                break;
            default:
                bookings = new PageImpl<>(Collections.emptyList());
                break;
        }
        return bookings
            .stream()
            .map(BookingMapper::toBookingDto)
            .collect(toList());
    }


    private void timeValidation(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ValidationException("Start and end time error");
        }
        if (end.equals(start)) {
            throw new ValidationException("Start and end time error");
        }
    }

}