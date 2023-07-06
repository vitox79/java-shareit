package ru.practicum.shareit.user;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void serializeUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .build();

        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
            .isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email")
            .isEqualTo("john.doe@example.com");
    }

    @Test
    void validateNotBlankName() {
        UserDto userDto = UserDto.builder()
            .id(1L)
            .name("")
            .email("john.doe@example.com")
            .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void validateNotBlankEmail() {
        UserDto userDto = UserDto.builder()
            .id(1L)
            .name("John Doe")
            .email("")
            .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void validateEmailFormat() {
        UserDto userDto = UserDto.builder()
            .id(1L)
            .name("John Doe")
            .email("invalid-email")
            .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }
}
