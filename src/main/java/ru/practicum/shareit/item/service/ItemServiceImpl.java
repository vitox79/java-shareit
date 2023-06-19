package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;
    private final RepositoryUser userRepository;

    private final CommentRepository commentRepository;

    private final ItemMapper itemMapper;

    private final BookingMapper bookingMapper;

    private final CommentMapper commentMapper;


    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElse(null);
        if (owner == null) {
            throw new DataNotFoundException("User not found");
        }
        itemDto.setOwner(owner.getId());
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setOwner(owner);
        itemRepository.save(newItem);
        return itemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto) {
        Optional<Item> existingItem = itemRepository.findById(itemId);
        if (!existingItem.isPresent()) {
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

        return itemMapper.toItemDto(existingItem.get());
    }


    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }
        ItemDto itemDto = itemMapper.toInfoItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now())
                .ifPresent(booking -> itemDto.setLastBooking(bookingMapper.toItemsBookingDto(booking)));

            bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(itemId,
                    LocalDateTime.now(), Status.REJECTED)
                .ifPresent(booking -> itemDto.setNextBooking(bookingMapper.toItemsBookingDto(booking)));
        }
        itemDto.setComments(commentRepository.findAllByItemId(item.getId()).orElse(List.of())
            .stream()
            .map(commentMapper::toCommentDto)
            .collect(toList()));


        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsById(long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())
                .ifPresent(booking -> itemDto.setLastBooking(bookingMapper.toItemsBookingDto(booking)));
            bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(item.getId(),
                    LocalDateTime.now(), Status.REJECTED)
                .ifPresent(booking -> itemDto.setNextBooking(bookingMapper.toItemsBookingDto(booking)));

            itemDto.setComments(commentRepository.findAllByItemId(item.getId()).orElse(List.of())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(toList()));

            itemsDto.add(itemDto);
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        return
            itemMapper.toItemDtoList(itemRepository.search(searchText));
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Optional<List<Booking>> bookings =
            bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(itemId, userId, LocalDateTime.now(),
                Status.REJECTED);
        if (bookings.isPresent() && !bookings.get().isEmpty()) {
            Comment comment =
                commentMapper.toComment(userRepository.findById(userId).get(), itemRepository.findById(itemId).get(),
                    commentDto, LocalDateTime.now());
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException(
                "You did not reserve this item, or the reservation period has not expired yet.");
        }
    }

}