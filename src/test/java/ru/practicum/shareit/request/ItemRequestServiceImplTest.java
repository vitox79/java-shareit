package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
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

class ItemRequestServiceImplTest {
    private final RepositoryUser userRepository = mock(RepositoryUser.class);

    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final ItemRequestService service =
        new ItemRequestServiceImpl(userRepository, itemRequestRepository, itemRepository);

    @Test
    void getAllByUserFromNegative() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getAllByUser(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeNegative() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getAllByUser(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeZero() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getAllByUser(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllWrongFrom() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getAll(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeNegative() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getAll(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeZero() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getAll(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getById(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyIncorrectCountException() {
        Throwable thrown = assertThrows(DataNotFoundException.class, () -> {
            service.getById(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyGetRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
            .description("desc")
            .id(1L)
            .created(LocalDateTime.now())
            .build();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest request = service.getById(1);

        assertEquals(itemRequest.getId(), request.getId(), "wrong id");
        assertEquals(itemRequest.getCreated(), request.getCreated(), "wrong created");
        assertEquals(itemRequest.getDescription(), request.getDescription(), "wrong description");
    }

    @Test
    void getByIdWithItemsEmpty() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder().id(1L).email("user").name("name").build()));
        ItemRequest request = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("d")
            .build();
        when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.of(request));
        when(itemRepository.findByRequestInOrderByIdAsc(any()))
            .thenReturn(List.of());

        ItemRequestDto requestDto = service.getById(1, 1);

        assertNotNull(requestDto, "wrong dto");
        assertEquals(request.getDescription(), requestDto.getDescription(), "wrong description");
        assertEquals(request.getId(), requestDto.getId(), "wrong id");
        assertEquals(0, requestDto.getItems().size(), "wrong items");
    }

    @Test
    void getByIdWithItems() {
        User user = User.builder().id(1L).email("user").name("name").build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
        ItemRequest request = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("desc")
            .owner(user)
            .build();
        when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.of(request));
        when(itemRepository.findByRequestInOrderByIdAsc(any()))
            .thenReturn(List.of(Item.builder().id(1L).owner(user).build()));

        ItemRequestDto requestDto = service.getById(1, 1);

        assertNotNull(requestDto, "null dto");
        assertEquals(request.getDescription(), requestDto.getDescription(), "wrong description");
        assertEquals(request.getId(), requestDto.getId(), "wrong id");
        assertEquals(1, requestDto.getItems().size(), "wrong size");
    }

    @Test
    void getAllEmpty() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder().id(1L).email("user").name("name").build()));
        when(itemRequestRepository.findByOwnerIdNot(anyLong(), any()))
            .thenReturn(Page.empty());

        List<ItemRequestDto> requests = service.getAll(1, 0, 1);

        assertNotNull(requests, "null");
        assertEquals(0, requests.size(), "wrong list size");
    }

    @Test
    void getAllByUserEmpty() {
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder().id(1L).email("user").name("name").build()));
        when(itemRequestRepository.findByOwnerId(anyLong(), any()))
            .thenReturn(Page.empty());

        List<ItemRequestDto> requests = service.getAllByUser(1, 0, 1);

        assertNotNull(requests, "null");
        assertEquals(0, requests.size(), "wrong list size");
    }

    @Test
    void getAllByUser() {
        LocalDateTime time = LocalDateTime.now();
        ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).description("d")
            .created(time).items(List.of()).build();
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(User.builder().id(1L).email("user").name("name").build()));
        when(itemRequestRepository.findByOwnerId(anyLong(), any()))
            .thenReturn(new PageImpl<>(List.of(ItemRequest.builder().id(1L).description("d")
                .created(time).build())));
        when(itemRepository.findByRequestInOrderByIdAsc(any())).thenReturn(List.of());

        List<ItemRequestDto> requests = service.getAllByUser(1, 0, 1);

        assertNotNull(requests, "null list");
        assertEquals(1, requests.size(), "empty list");
        assertEquals(requestDto, requests.get(0), "wrong dto");
        assertEquals(0, requests.get(0).getItems().size(), "wrong items");
    }

}