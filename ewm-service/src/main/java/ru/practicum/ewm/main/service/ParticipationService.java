package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.model.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.model.dto.event.EventRequestStatusUpdateResult;

import java.util.List;

public interface ParticipationService {
    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult setRequestsStatusResults(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
