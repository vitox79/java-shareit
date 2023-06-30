package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Invalid item data");
            throw new NotFoundException("Invalid user data");
        }
        User createdUser = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.createUser(createdUser));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return UserMapper.toItemDtoList(users);
    }

}
