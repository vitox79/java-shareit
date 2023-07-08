package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;


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

}