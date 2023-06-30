package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @RequestMapping("/add-item")
    public String addItem() {
        return "true";
    }
}
