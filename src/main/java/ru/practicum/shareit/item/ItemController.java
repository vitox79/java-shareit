package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                           @Valid @RequestBody ItemDto itemDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Invalid item data");
            throw new NotFoundException("Invalid item data");
        }

        if (userId == null) {
            throw new NotFoundException("\"X-Sharer-User-Id not exist");
        }

        return itemService.addItem(userId, itemDto);

    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                            @PathVariable long itemId, @RequestBody ItemDto itemDto, BindingResult bindingResult) {
        if (userId == null) {
            throw new NotFoundException("\"X-Sharer-User-Id not exist");
        }
        itemDto.setOwner(userId);
        return itemService.editItem(itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                               @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                      @Positive @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemsById(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String searchText,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                     @Positive @RequestParam(defaultValue = "10") int size) {
        if (searchText.isEmpty()) return new ArrayList<ItemDto>();
        return itemService.searchItems(searchText, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }


}
