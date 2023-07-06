package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingDtoTest {
    private final BookingDto bookingDto = BookingDto.builder()
        .end(LocalDateTime.now())
        .start(LocalDateTime.now())
        .build();
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateNullEndBooker() {
        bookingDto.setEnd(null);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "null email is created");
    }

    @Test
    void validateNullStartBooker() {
        bookingDto.setStart(null);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "null email is created");
    }
}