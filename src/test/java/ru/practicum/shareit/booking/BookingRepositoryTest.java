package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository repository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RepositoryUser userRepository;
    private User user;
    private Item item;
    private Booking booking;
    private Booking booking1;

    @BeforeEach
    void init() {
        repository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder().id(1L).name("user").email("name@mail.ru").build());
        item = itemRepository.save(Item.builder().id(1L).owner(user).name("user").description("what").build());
        booking = Booking.builder().status(Status.WAITING).item(item)
            .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        booking1 = Booking.builder().status(Status.REJECTED).item(item)
            .end(LocalDateTime.now()).start(LocalDateTime.now()).build();


    }

    @Test
    void findByOwnerIdAndStatusIn() {

        booking = repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings =
            repository.findByOwnerIdAndStatusIn(user.getId(), Set.of(Status.REJECTED, Status.CANCELED),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Null");
        assertEquals(1, bookings.size(), "Wrong size 1");
        assertEquals(booking1, bookings.get(0), "Wrong in 0");
    }

    @Test
    void findByOwnerIdAndStatus() {

        repository.save(booking);
        booking1 = repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdAndStatus(user.getId(), Status.REJECTED,
            PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Null data");
        assertEquals(1, bookings.size(), "Wrong size list");
        assertEquals(booking1, bookings.get(0), "Wrong 1");
    }

    @Test
    void findByOwnerId() {

        booking = repository.save(booking);
        booking1 = repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerId(user.getId(), PageRequest.of(0, 2))
            .stream().collect(toList());

        assertNotNull(bookings, "Null data");
        assertEquals(2, bookings.size(), "Wrong list size");
        assertEquals(booking, bookings.get(0), "Wrong in 0");
        assertEquals(booking1, bookings.get(1), "Wrong in 1");
    }

    @Test
    void findByOwnerIdCurrent() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
            .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
            .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now().minusDays(1)).build();


        repository.save(booking);
        booking1 = repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdCurrent(user.getId(), LocalDateTime.now(),
            PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Null data");
        assertEquals(1, bookings.size(), "Wrong size");
        assertEquals(booking1, bookings.get(0), "Wrong in 0");
    }

    @Test
    void findByOwnerIdPast() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
            .end(LocalDateTime.now().plusHours(1)).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
            .end(LocalDateTime.now().minusHours(1)).start(LocalDateTime.now()).build();

        repository.save(booking);
        booking1 = repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdPast(user.getId(), LocalDateTime.now(),
            PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Null data");
        assertEquals(1, bookings.size(), "Wrong size");
        assertEquals(booking1, bookings.get(0), "Wrong in 0");
    }
}



