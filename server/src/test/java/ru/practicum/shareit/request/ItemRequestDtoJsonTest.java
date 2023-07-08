package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;


    @Test
    void serializeItemRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        List<ItemDto> items = new ArrayList<>();
        ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .owner(1L)
            .requestId(1L)
            .name("item name")
            .description("item description")
            .available(true)
            .build();
        items.add(itemDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("request description")
            .created(created)
            .items(items)
            .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
            .isEqualTo("request description");
        assertThat(result).extractingJsonPathStringValue("$.created")
            .isEqualTo(created.truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathArrayValue("$.items")
            .hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].owner")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
            .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
            .isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
            .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
            .isEqualTo(true);
    }


}
