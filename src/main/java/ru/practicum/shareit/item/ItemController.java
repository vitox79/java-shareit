package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @Valid @RequestBody ItemDto itemDto, BindingResult bindingResult) {
        System.out.println(itemDto);
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
    public ItemDto editItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto, BindingResult bindingResult) {
        if (userId == null) {
            throw new NotFoundException("\"X-Sharer-User-Id not exist");
        }
        itemDto.setOwnerId(userId);
        return itemService.editItem(itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {

        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String searchText) {
        if (searchText.isEmpty()) return new ArrayList<ItemDto>();
        return itemService.searchItems(searchText);
    }

}