package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "requestor", source = "itemRequest.requestor")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requestor", source = "itemRequestDto.requestor")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);
}