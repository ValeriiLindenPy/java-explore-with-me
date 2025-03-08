package ru.practicum.ewm.main.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exceptions.ConflictException;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.Participation;
import ru.practicum.ewm.main.model.QEvent;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.model.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.model.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.model.enums.EventState;
import ru.practicum.ewm.main.model.enums.RequestStatus;
import ru.practicum.ewm.main.model.mapper.ParticipationMapper;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.ParticipationRepository;
import ru.practicum.ewm.main.repository.UserRepository;
import ru.practicum.ewm.main.service.ParticipationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        return repository.findByEvent_Id(eventId).stream()
                .filter(participation -> Objects.equals(participation.getEvent().getInitiator().getId(), userId))
                .map(ParticipationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult setRequestsStatusResults(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("Confirmation for this event isn't needed");
        }

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            throw new ConflictException("The participant limit has been reached");
        }

        List<Participation> requests = repository.findByIdInAndEventId(updateRequest.getRequestIds(), eventId);

        for (Participation request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Only pending requests status can be changed");
            }
        }

        int confirmedCount = event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0;
        int participantLimit = event.getParticipantLimit();

        List<Participation> confirmedRequests = new ArrayList<>();
        List<Participation> rejectedRequests = new ArrayList<>();
        QEvent qEvent = QEvent.event;

        if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
            for (Participation request : requests) {
                if (confirmedCount >= participantLimit) {
                    throw new ConflictException("The participant limit has been reached");
                }

                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);

                queryFactory.update(qEvent)
                        .set(qEvent.confirmedRequests, qEvent.confirmedRequests.add(1))
                        .where(qEvent.id.eq(request.getEvent().getId()))
                        .execute();

                confirmedCount++;

                if (confirmedCount == participantLimit) {
                    List<Participation> pendingRequests = repository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);

                    for (Participation pendingRequest : pendingRequests) {
                        pendingRequest.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(pendingRequest);
                    }

                    repository.saveAll(rejectedRequests);

                    break;
                }
            }

            repository.saveAll(confirmedRequests);

            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(confirmedRequests.stream().map(ParticipationMapper::toDto).toList())
                    .rejectedRequests(rejectedRequests.stream().map(ParticipationMapper::toDto).toList())
                    .build();
        } else if (updateRequest.getStatus() == RequestStatus.REJECTED) {
            for (Participation request : requests) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }

            repository.saveAll(rejectedRequests);

            return EventRequestStatusUpdateResult.builder()
                    .rejectedRequests(rejectedRequests.stream().map(ParticipationMapper::toDto).toList())
                    .build();
        } else {
            throw new ConflictException("Wrong update status");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d was not found".formatted(userId))
        );

        return repository.findByRequesterId(userId).stream()
                .map(ParticipationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d was not found".formatted(userId))
        );

        List<Participation> participations = repository.findByEvent_IdAndRequester_Id(eventId, userId);

        if (!participations.isEmpty()) {
            throw new ConflictException("Duplication request for the event.");
        }

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Initiator can't be a participant in its event.");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("This event hasn't yet published.");
        }

        if (event.getParticipantLimit() != 0) {
            if (Objects.equals(event.getParticipantLimit(), event.getConfirmedRequests())) {
                throw new ConflictException("The participant limit has been reached");
            }
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            QEvent qEvent = QEvent.event;

            queryFactory.update(qEvent)
                    .set(qEvent.confirmedRequests, qEvent.confirmedRequests.add(1))
                    .where(qEvent.id.eq(event.getId()))
                    .execute();

            return ParticipationMapper.toDto(repository.save(Participation.builder()
                    .event(event)
                    .requester(requester)
                    .status(RequestStatus.CONFIRMED)
                    .build()));
        }

        return ParticipationMapper.toDto(repository.save(Participation.builder()
                .event(event)
                .requester(requester)
                .status(RequestStatus.PENDING)
                .build()));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Participation request = repository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException("Request with id=%d was not found".formatted(requestId))
        );

        request.setStatus(RequestStatus.CANCELED);

        return ParticipationMapper.toDto(repository.save(request));
    }
}
