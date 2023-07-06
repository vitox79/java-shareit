package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;
    private final RepositoryUser userRepository;
    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElse(null);
        if (owner == null) {
            throw new DataNotFoundException("User not found");
        }
        itemDto.setOwner(owner.getId());
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            newItem.setRequest(getRequestById(itemDto.getRequestId()));
        }
        itemRepository.save(newItem);
        return ItemMapper.toInfoItemDto(newItem);
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto) {
        Optional<Item> existingItem = itemRepository.findById(itemId);
        if (existingItem.isEmpty()) {
            throw new DataNotFoundException("Item not found");
        }

        if (existingItem.get().getOwner().getId() != (itemDto.getOwner())) {
            throw new DataNotFoundException("User is not the owner of the item");
        }

        if (itemDto.getName() != null) {
            existingItem.get().setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.get().setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.get().setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(existingItem.get());

        return ItemMapper.toItemDto(existingItem.get());
    }


    @Override
    public ItemDto getItemById(long userId, long itemId) {

        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }
        ItemDto itemDto = ItemMapper.toInfoItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now())
                .ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingInfoDto(booking)));

            bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(itemId,
                    LocalDateTime.now(), Status.REJECTED)
                .ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingInfoDto(booking)));
        }
        itemDto.setComments(
            commentRepository.findAllByItemId(item.getId()).orElse(List.of()).stream().map(CommentMapper::toCommentDto)
                .collect(toList()));


        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsById(long ownerId, int from, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("wrong size ");
        }
        int pageCount = (from + size - 1) / size;

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(ownerId, PageRequest.of(pageCount, size)).stream()
            .collect(toList());

        Map<Item, List<Comment>> comments =
            commentRepository.findByItemInOrderByCreatedDesc(items, Sort.by(Sort.Direction.DESC, "created")).stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        Map<Item, List<Booking>> nextBookings =
            bookingRepository.findApprovedNextForItems(items, Sort.by(Sort.Direction.DESC, "start"),
                LocalDateTime.now()).stream().collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));

        Map<Item, List<Booking>> lastBookings =
            bookingRepository.findApprovedLastForItems(items, Sort.by(Sort.Direction.DESC, "start"),
                LocalDateTime.now()).stream().collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));


        List<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);

            if (lastBookings.get(item) != null && !lastBookings.get(item).isEmpty()) {
                Booking lastBooking = lastBookings.get(item).get(0);
                itemDto.setLastBooking(BookingMapper.toBookingInfoDto(lastBooking));
            }

            if (nextBookings.get(item) != null && nextBookings.get(item).size() > 1) {
                Booking nextBooking = nextBookings.get(item).get(1);
                itemDto.setNextBooking(BookingMapper.toBookingInfoDto(nextBooking));
            }

            List<Comment> itemComments = comments.get(item);
            if (itemComments != null && !itemComments.isEmpty()) {
                List<CommentDto> commentDto =
                    itemComments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
                itemDto.setComments(commentDto);
            }

            itemsDto.add(itemDto);
        }

        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String searchText, int from, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("wrong size ");
        }
        int pageCount = (from + size - 1) / size;

        return ItemMapper.toItemDtoList(itemRepository.search(searchText, PageRequest.of(pageCount, size)).stream()
            .collect(toList()));
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Optional<List<Booking>> bookings =
            bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(itemId, userId, LocalDateTime.now(),
                Status.REJECTED);
        if (bookings.isPresent() && !bookings.get().isEmpty()) {
            Comment comment =
                CommentMapper.toComment(userRepository.findById(userId).get(), itemRepository.findById(itemId).get(),
                    commentDto, LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException(
                "You did not reserve this item, or the reservation period has not expired yet.");
        }
    }

    @Override
    public ItemRequest getRequestById(long requestId) {
        if (requestId < 0) {
            throw new DataNotFoundException("Id should be positive.");
        }
        Optional<ItemRequest> optional = itemRequestRepository.findById(requestId);
        return optional.orElseThrow(() -> new DataNotFoundException(String.format("Id not found.", requestId)));
    }


}