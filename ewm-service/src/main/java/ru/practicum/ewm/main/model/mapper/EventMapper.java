package ru.practicum.ewm.main.model.mapper;

import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.dto.event.EventFullDto;
import ru.practicum.ewm.main.model.dto.event.EventShortDto;

public class EventMapper {

    public static EventFullDto toDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .views(event.getViews().size())
                .state(event.getState())
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .views(event.getViews().size())
                .eventDate(event.getEventDate())
                .confirmedRequests(event.getConfirmedRequests())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.toDto(event.getCategory()))
                .build();
    }
}
