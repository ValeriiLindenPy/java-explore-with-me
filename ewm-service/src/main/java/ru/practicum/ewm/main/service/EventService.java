package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.model.dto.event.*;

import java.util.List;

public interface EventService {
    EventFullDto getOne(Long id);

    EventFullDto getOne(Long userId, Long eventId);

    List<EventShortDto> getAll(EventFilterDto filterDto);

    List<EventFullDto> getAll(AdminEventFilterDto filterDto);

    List<EventShortDto> getAll(Long userId, Integer from, Integer size);

    EventFullDto update(Long eventId, UpdateEventAdminRequest request);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest request);

    EventFullDto create(Long userId, NewEventDto eventDto);
}
