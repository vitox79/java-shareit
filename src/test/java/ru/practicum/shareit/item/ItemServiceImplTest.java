package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    private final ItemRepository repository = mock(ItemRepository.class);

    private final RepositoryUser userRepository = mock(RepositoryUser.class);

    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);

    private final CommentRepository commentRepository = mock(CommentRepository.class);


    private ItemService service =
        new ItemServiceImpl(repository, bookingRepository, userRepository,
            commentRepository, itemRequestRepository);

    @Test
    void updateNoOwner() {

        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(Item.builder()
                .owner(User.builder().id(2L).name("user").email("mail").build())
                .available(true)
                .name("user")
                .description("description").build()));

        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.editItem(1, mock(ItemDto.class));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllFromWrong() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getItemsById(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeWrong() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getItemsById(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getItemsById(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void searchTextFromWrong() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.searchItems("text", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void searchTextSizeWrong() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.searchItems("text", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void searchTextSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.searchItems("text", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }


    @Test
    void addCommentEmptyBooking() {
        when(bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(anyLong(), anyLong(), any(), any()))
            .thenReturn(Optional.of(Collections.emptyList()));

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.addComment(1, 1, mock(CommentDto.class));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getItemByIdZero() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getItemById(1, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateNameDescriptionAndAvailable() {
        service = new ItemServiceImpl(repository, bookingRepository, userRepository,
            commentRepository, itemRequestRepository);
        when(repository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
            .owner(User.builder().id(1L).build())
            .name("name")
            .description("Desc")
            .available(false).build()));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();
        update.setOwner(1L);
        ItemDto itemDto = service.editItem(1, update);

        assertNotNull(itemDto, "Null Dto");
        assertEquals(update.getAvailable(), itemDto.getAvailable(), "wrong available");
        assertEquals(update.getDescription(), itemDto.getDescription(), "wrong description");
        assertEquals(update.getName(), itemDto.getName(), "wrong name");

    }

    @Test
    void updateName() {

        service = new ItemServiceImpl(repository, bookingRepository, userRepository,
            commentRepository, itemRequestRepository);
        Item itemRepository = Item.builder()
            .owner(User.builder().id(1L).build())
            .name("name")
            .description("Desc")
            .available(false).build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().name("java").build();
        update.setOwner(1L);
        ItemDto itemDto = service.editItem(1, update);

        assertNotNull(itemDto, "Null Dto");
        assertEquals(itemRepository.getDescription(), itemDto.getDescription(), "Wrong description");
        assertEquals(update.getName(), itemDto.getName(), "Wrong name");
        assertEquals(itemRepository.isAvailable(), itemDto.getAvailable(), "Wrong available");

    }

    @Test
    void updateDescription() {

        service = new ItemServiceImpl(repository, bookingRepository, userRepository,
            commentRepository, itemRequestRepository);

        Item itemRepository = Item.builder()
            .owner(User.builder().id(1L).build())
            .name("name")
            .description("D")
            .available(false).build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().available(true).description("d2").name("apple").build();
        update.setOwner(1L);
        ItemDto itemDto = service.editItem(1, update);

        assertNotNull(itemDto, "Null Dto");
        assertEquals(itemRepository.isAvailable(), itemDto.getAvailable(), "Wrong available");
        assertEquals(update.getDescription(), itemDto.getDescription(), "Wrong description");
        assertEquals(itemRepository.getName(), itemDto.getName(), "Wrong name");

    }

    @Test
    void updateAvailable() {

        service = new ItemServiceImpl(repository, bookingRepository, userRepository,
            commentRepository, itemRequestRepository);

        Item itemRepository = Item.builder()
            .owner(User.builder().id(1L).build())
            .name("name")
            .description("D")
            .available(false).build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();
        update.setOwner(1L);
        ItemDto itemDto = service.editItem(1, update);

        assertNotNull(itemDto, "Null Dto");
        assertEquals(update.getAvailable(), itemDto.getAvailable(), "Wrong available");
        assertEquals(itemRepository.getDescription(), itemDto.getDescription(), "Wrong description");
        assertEquals(itemRepository.getName(), itemDto.getName(), "Wrong name");

    }

    @Test
    void addComment() {

        when(bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(anyLong(), anyLong(),
            any(), any()))
            .thenReturn(Optional.of(List.of(Booking.builder().id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now()).build())));
        User user = User.builder().name("name").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        LocalDateTime time = LocalDateTime.now();
        when(repository.findById(anyLong())).thenReturn(Optional.of(Item.builder().build()));
        when(commentRepository.save(any())).thenReturn(Comment.builder().id(1L)
            .created(time)
            .author(user)
            .item(Item.builder().build())
            .text("text").build());
        CommentDto commentDto = CommentDto.builder().text("text").build();

        CommentDto commentDto1 = service.addComment(1, 1, commentDto);

        assertEquals(commentDto.getText(), commentDto1.getText(), "wrong test");
        assertEquals(user.getName(), commentDto1.getAuthorName(), "wrong name");
        assertEquals(time, commentDto1.getCreated(), "wrong time");
    }

    @Test
    void searchItems() {
        when(repository.search(anyString(), any())).thenReturn(Page.empty());

        List<ItemDto> itemDtos = service.searchItems("text", 0, 1);

        assertEquals(0, itemDtos.size(), "wrong search");
    }

    @Test
    void getAllEmpty() {
        when(repository.findByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(Page.empty());

        List<ItemDto> itemDto = service.getItemsById(1, 0, 1);

        assertEquals(0, itemDto.size(), "wrong search");
    }

    @Test
    void getAllBooking() {

        service = new ItemServiceImpl(repository, bookingRepository, userRepository,
            commentRepository, itemRequestRepository);
        User user = User.builder().name("name").id(4L).email("email").build();
        Item item = Item.builder().id(1L).name("1").owner(user).description("1").build();
        Item item1 = Item.builder().id(2L).name("2").owner(user).description("2").build();
        Item item2 = Item.builder().id(3L).name("3").owner(user).description("3").build();
        when(repository.findByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(
            new PageImpl<>(List.of(item2, item, item1)));

        Booking bookingLast1 =
            Booking.builder().id(2L).end(LocalDateTime.now().minusHours(1)).start(LocalDateTime.now().minusHours(2))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingLast2 =
            Booking.builder().id(1L).end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now().minusDays(2))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingLast3 =
            Booking.builder().id(3L).end(LocalDateTime.now().minusMonths(1)).start(LocalDateTime.now().minusMonths(2))
                .item(item2).booker(User.builder().id(1L).build()).build();
        when(bookingRepository.findApprovedLastForItems(any(), any(), any()))
            .thenReturn(List.of(bookingLast1, bookingLast2, bookingLast3));

        Booking bookingNext1 =
            Booking.builder().id(3L).end(LocalDateTime.now().plusHours(1)).start(LocalDateTime.now().plusHours(2))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingNext2 =
            Booking.builder().id(1L).end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now().plusDays(2))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingNext3 =
            Booking.builder().id(2L).end(LocalDateTime.now().plusMonths(1)).start(LocalDateTime.now().plusMonths(2))
                .item(item2).booker(User.builder().id(1L).build()).build();
        when(bookingRepository.findApprovedNextForItems(any(), any(), any()))
            .thenReturn(List.of(bookingNext1, bookingNext2, bookingNext3));

        when(commentRepository.findByItemInOrderByCreatedDesc(any(), any())).thenReturn(List.of(Comment.builder()
            .id(1L)
            .text("comment")
            .item(item)
            .author(User.builder().name("name").build())
            .build()));

        List<ItemDto> itemDto = service.getItemsById(1, 0, 5);

        BookingInfoDto bookingDtoLast = BookingInfoDto.builder().bookerId(1L).id(2L).build();
        BookingInfoDto bookingDtoNext = BookingInfoDto.builder().bookerId(1L).id(1L).build();
        CommentDto commentDto = CommentDto.builder().id(1L).text("comment").authorName("name").build();

        assertEquals(bookingDtoLast, itemDto.get(0).getLastBooking(), "wrong lb");
        assertEquals(bookingDtoNext, itemDto.get(0).getNextBooking(), "wrong nb");
        assertNull(itemDto.get(1).getNextBooking(), "should be null");
        assertNull(itemDto.get(1).getLastBooking(), "should be null");
        assertEquals(commentDto, itemDto.get(1).getComments().get(0), "should be null");
        assertNull(itemDto.get(2).getNextBooking(), "should be null");
        assertNull(itemDto.get(2).getLastBooking(), "should be null");
    }

    @Test
    void getItemByIdInvalidId() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getItemById(0, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void addCommentNoItem() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.addComment(1, 1, mock(CommentDto.class));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void searchItemsInvalidPageSize() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.searchItems("text", 1, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void addItemNoUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.addItem(1L, ItemDto.builder().id(1L).build());
        });
    }

    @Test
    void editItemNull() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.editItem(1L, ItemDto.builder().id(1L).build());
        });
    }

    @Test
    void getItemById() {
        long userId = 1L;
        long itemId = 1L;

        when(repository.findById(itemId)).thenReturn(Optional.of(Item
            .builder()
            .id(1L)
            .owner(User.builder().id(1L).build())
            .name("item")
            .description("d")
            .available(true)
            .request(null)
            .build()));

        when(commentRepository.findAllByItemId(itemId)).thenReturn(Optional.of(List.of()));
        when(bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(
            itemId, LocalDateTime.now())).thenReturn(Optional.of(new Booking()));
        when(bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(
            itemId, LocalDateTime.now(), Status.REJECTED)).thenReturn(Optional.of(new Booking()));

        ItemDto result = service.getItemById(userId, itemId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("item", result.getName());
    }

    @Test
    void getRequestById() {
        long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequest result = service.getRequestById(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }


}