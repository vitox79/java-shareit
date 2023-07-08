package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.UnknownArgumentException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    private final RepositoryUser userRepository = mock(RepositoryUser.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final BookingRepository repository = mock(BookingRepository.class);

    private BookingService service = new BookingServiceImpl(repository, userRepository, itemRepository);

    private final BookingDto bookingDto =
        BookingDto.builder().end(LocalDateTime.now()).start(LocalDateTime.now()).itemId(1L).build();

    @Test
    void createAvailableIsFalse() {
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(Item.builder()
                .available(false)
                .name("name")
                .description("qwe")
                .build()));
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder()
                .name("name")
                .email("mail")
                .id(1L)
                .build()));

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.create(1, BookingRequestDto.builder().itemId(1L).end(LocalDateTime.now())
                .start(LocalDateTime.now())
                .build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void createWrongOwner() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder()
                .name("name")
                .email("mail")
                .id(1L)
                .build()));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(Item.builder()
                .owner(User.builder().id(1L).name("name").email("mail").build())
                .available(true)
                .name("name")
                .description("qwe")
                .build()));

        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.create(1, BookingRequestDto.builder()
                .itemId(1L)
                .end(LocalDateTime.now().plusHours(2))
                .start(LocalDateTime.now().plusHours(1))
                .build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTime() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder()
                .name("name")
                .email("mail")
                .id(1L)
                .build()));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(Item.builder()
                .owner(User.builder().id(2L).name("name").email("mail").build())
                .available(true)
                .name("name")
                .description("qwe")
                .build()));

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, BookingRequestDto.builder()
                .itemId(1L)
                .end(LocalDateTime.now().plusHours(1))
                .start(LocalDateTime.now().plusHours(2))
                .build());
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTimeEqual() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder()
                .name("name")
                .email("mail")
                .id(1L)
                .build()));
        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(Item.builder()
                .owner(User.builder().id(2L).name("name").email("mail").build())
                .available(true)
                .name("name")
                .description("qwe")
                .build()));

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, BookingRequestDto.builder()
                .itemId(1L)
                .end(LocalDateTime.now().plusHours(2))
                .start(LocalDateTime.now().plusHours(2))
                .build());
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionUnknown() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getById(1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionNoOwner() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(Booking.builder()
            .booker(User.builder().id(2L).email("user").name("name").build())
            .item(Item.builder()
                .owner(User.builder().id(2L).name("name").email("user").build())
                .available(true)
                .name("name")
                .description("description")
                .build())
            .end(LocalDateTime.now().plusDays(1))
            .start(LocalDateTime.now().plusHours(1))
            .build()));
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getById(1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserFrom() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getAllByUser(1, "ALL", -1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSize() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, "ALL", 0, -1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, "ALL", 0, 0);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserStateUnknown() {
        Throwable thrown = assertThrows(UnknownArgumentException.class, () -> {
            service.getAllByUser(1, "some", 0, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerStateUnknown() {
        Throwable thrown = assertThrows(UnknownArgumentException.class, () -> {
            service.getAllByOwner(1, "some", 0, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateApprovedIsNull() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.update(1, 1, null);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateNoOwner() {
        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                    .owner(User.builder().id(2L).name("name").email("email").build())
                    .available(true)
                    .name("name")
                    .description("description")
                    .build())
                .build()));
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.update(1, 1, false);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateStatusApproved() {
        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                    .owner(User.builder().id(1L).name("name").email("email").build())
                    .available(true)
                    .name("name")
                    .description("description")
                    .build())
                .status(Status.APPROVED)
                .build()));
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.update(1, 1, false);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateStatusRejected() {
        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                    .owner(User.builder().id(1L).name("name").email("email").build())
                    .available(true)
                    .name("name")
                    .description("description")
                    .build())
                .status(Status.REJECTED)
                .build()));
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.update(1, 1, false);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getByIdWhenOwnerItemWithMapper() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        Booking booking = Booking.builder()
            .id(1L)
            .status(Status.WAITING)
            .booker(User.builder().id(2L).name("name").email("user@mail").build())
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(Item.builder()
                .owner(User.builder().id(1L).name("name").email("user@mail").build())
                .available(true)
                .name("name")
                .description("description")
                .build())
            .build();
        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder().name("name").id(1L).email("email").build()));
        BookingDto bookingDto = service.getById(1, 1);

        assertNotNull(bookingDto, "null Dto");
        assertEquals(booking.getStart(), bookingDto.getStart(), "wrong start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "wrong end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "wrong item");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getByIdWhenBookerWithMapper() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder().name("name").id(1L).email("email").build()));
        Booking booking = Booking.builder()
            .booker(User.builder().id(2L).name("name").email("email").build())
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .status(Status.WAITING)
            .item(Item.builder()
                .owner(User.builder().id(1L).name("name").email("email").build())
                .available(true)
                .name("name")
                .description("description")
                .build())
            .build();
        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.getById(2, 1);

        assertNotNull(bookingDto, "null Dto");
        assertEquals(booking.getStart(), bookingDto.getStart(), "wrong start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "wrong end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "wrong item");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void updateWhenApprovedTrue() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        Booking booking = Booking.builder()
            .status(Status.WAITING)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(Item.builder()
                .owner(User.builder().id(1L).name("name").email("email").build())
                .available(true)
                .name("name")
                .description("d")
                .build())
            .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);
        BookingDto bookingDto = service.update(1, 1, true);

        assertNotNull(bookingDto, "null Dto");
        assertEquals(booking.getStart(), bookingDto.getStart(), "wrong start start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "wrong end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "wrong item");
        assertEquals(Status.APPROVED.toString(), bookingDto.getStatus(), "wrong status");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void updateWhenApprovedFalse() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        Booking booking = Booking.builder()
            .status(Status.WAITING)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(Item.builder()
                .owner(User.builder().id(1L).name("name").email("email").build())
                .available(true)
                .name("name")
                .description("d")
                .build())
            .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        BookingDto bookingDto = service.update(1, 1, false);

        assertNotNull(bookingDto, "null Dto");
        assertEquals(booking.getStart(), bookingDto.getStart(), "wrong start start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "wrong end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "wrong item");
        assertEquals(Status.REJECTED.toString(), bookingDto.getStatus(), "wrong status");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerStateAll() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByItem_OwnerIdOrderByStartDesc(anyLong(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "ALL", 0, 1);

        assertNotNull(bookingDtos, "null Dto");
        assertEquals(0, bookingDtos.size(), "wrong list");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerStateFUTURE() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByOwnerIdAndStatusIn(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "FUTURE", 0, 1);

        assertNotNull(bookingDtos, "null Dto");
        assertEquals(0, bookingDtos.size(), "wrong list");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerStateREJECTED() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByOwnerIdAndStatus(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "REJECTED", 0, 1);

        assertNotNull(bookingDtos, "null dto");
        assertEquals(0, bookingDtos.size(), "wrong list");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerStateWAITING() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByOwnerIdAndStatus(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "WAITING", 0, 1);

        assertNotNull(bookingDtos, "null dto");
        assertEquals(0, bookingDtos.size(), "wrong list");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerStateCURRENT() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByOwnerIdCurrent(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "CURRENT", 0, 1);

        assertNotNull(bookingDtos, "null Dto");
        assertEquals(0, bookingDtos.size(), "wrong list");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByUserStateAll() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "ALL", 0, 1);

        assertNotNull(bookingDtos, "null dto");
        assertEquals(0, bookingDtos.size(), "wrong list");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerStatePAST() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByOwnerIdPast(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "PAST", 0, 1);

        assertNotNull(bookingDtos, "null Dto");
        assertEquals(0, bookingDtos.size(), "wrong list");


        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByUserStateFUTURE() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByBookerIdAndStatusInOrderByStartDesc(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "FUTURE", 0, 1);

        assertNotNull(bookingDtos, "null dto");
        assertEquals(0, bookingDtos.size(), "wrong list");


        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByUserStateWAITING() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "WAITING", 0, 1);

        assertNotNull(bookingDtos, "null dto");
        assertEquals(0, bookingDtos.size(), "wrong list");


        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByUserStateREJECTED() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "REJECTED", 0, 1);

        assertNotNull(bookingDtos, "null dto");
        assertEquals(0, bookingDtos.size(), "wrong list");


        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }

    @Test
    void getAllByUserStatePAST() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "PAST", 0, 1);

        assertNotNull(bookingDtos, "null list");
        assertEquals(0, bookingDtos.size(), "wrong list");

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }


    @Test
    void getAllByUserStateCURRENT() {

        service = new BookingServiceImpl(repository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(repository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
            .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "CURRENT", 0, 1);

        assertNotNull(bookingDtos, "null dto");
        assertEquals(0, bookingDtos.size(), "wrong list");


        service = new BookingServiceImpl(repository, userRepository, itemRepository);
    }


}