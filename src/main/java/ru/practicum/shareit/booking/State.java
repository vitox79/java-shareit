package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.model.UnknownArgumentException;

public enum State {
    ALL, FUTURE, REJECTED, WAITING, CURRENT, PAST;

    public static State fromString(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownArgumentException("Unknown state: " + state);
        }
    }
}
