package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SimpleRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.RepositoryUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RepositoryUser userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto add(long userId, SimpleRequestDto requestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto);
        itemRequest.setOwner(userRepository.findById(userId).get());
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        if (userRepository.findById(userId).isEmpty()){
            throw new DataNotFoundException("User not found");
        }
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(getById(requestId));
        requestDto.setItems(itemRepository.findByRequestInOrderByIdAsc(List.of(getById(requestId)))
            .stream()
            .map(ItemMapper::toInfoItemDto)
            .collect(Collectors.toList()));
        return requestDto;
    }

    @Override
    public List<ItemRequestDto> getAllByUser(long userId, int from, int size) {
        if (userRepository.findById(userId).isEmpty()){
            throw new DataNotFoundException("User not found");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("wrong size ");
        }
        int pageCount = (from + size - 1) / size;
        Page<ItemRequest> requests = requestRepository.findByOwnerId(userId, PageRequest.of(pageCount, size, Sort.by("created")));
        return getItemsByRequests(requests);
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        if (userRepository.findById(userId).isEmpty()){
            throw new DataNotFoundException("User not found");
        }
        int pageCount = (from + size - 1) / size;
        Page<ItemRequest> requests = requestRepository.findByOwnerIdNot(userId, PageRequest.of(pageCount, size, Sort.by("created").descending()));
        return getItemsByRequests(requests);
    }

    @Override
    public ItemRequest getById(long requestId) {
        if (requestId < 0) {
            throw new DataNotFoundException("Id should be positive.");
        }
        Optional<ItemRequest> optional = requestRepository.findById(requestId);
        return optional.orElseThrow(() -> new DataNotFoundException(String.format("Id not found.", requestId)));
    }

    private List<ItemRequestDto> getItemsByRequests(Page<ItemRequest> requests) {
        List<ItemRequestDto> requestsDto = requests.stream()
            .map(ItemRequestMapper::toItemRequestDto)
            .collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemsMap = itemRepository.findByRequestInOrderByIdAsc(requests.toList())
            .stream()
            .filter(item -> item.getRequest() != null)
            .collect(groupingBy(item -> item.getRequest().getId(), Collectors.mapping(ItemMapper::toInfoItemDto, Collectors.toList())));

        for (ItemRequestDto requestDto : requestsDto) {
            requestDto.setItems(itemsMap.getOrDefault(requestDto.getId(), List.of()));
        }
        return requestsDto;
    }
}
