package ru.practicum.ewm.main.controller.event;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.event.EventFilterDto;
import ru.practicum.ewm.main.model.dto.event.EventFullDto;
import ru.practicum.ewm.main.model.dto.event.EventShortDto;
import ru.practicum.ewm.main.service.EventService;
import ru.practicum.stats.client.StatsClient;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService service;
    private final StatsClient statsClient;
    @Value("spring.application.name")
    private String applicationName;

    @GetMapping
    public List<EventShortDto> getAll(@ModelAttribute EventFilterDto filterDto, HttpServletRequest request) {
        statsClient.postHit(applicationName, request.getRequestURI(), request.getRemoteAddr());
        return service.getAll(filterDto);
    }

    @GetMapping("/{id}")
    public EventFullDto getOne(@PathVariable("id") Long id, HttpServletRequest request) {
        statsClient.postHit(applicationName, request.getRequestURI(), request.getRemoteAddr());
        return service.getOne(id, request.getRemoteAddr());
    }
}
