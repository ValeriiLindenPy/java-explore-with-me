package ru.practicum.ewm.main.controller.event.participation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.service.ParticipationService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationService service;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable("userId") Long userId) {
        return service.getRequestsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId, @RequestParam Long eventId) {
        return service.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}
