package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody BookingRequestDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                    @RequestParam(required = false) Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        System.out.println(from);
        return bookingService.getAllByUser(userId, state, from, size);

    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }

}