package ru.practicum.ewm.main.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.event.AdminEventFilterDto;
import ru.practicum.ewm.main.model.dto.event.EventFullDto;
import ru.practicum.ewm.main.model.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.main.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> getAll(@ModelAttribute AdminEventFilterDto filterDto) {
        return service.getAll(filterDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable("eventId") Long eventId,
                               @RequestBody @Valid UpdateEventAdminRequest request) {
        return service.update(eventId, request);
    }
}
