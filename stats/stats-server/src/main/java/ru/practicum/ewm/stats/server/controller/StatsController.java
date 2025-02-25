package ru.practicum.ewm.stats.server.controller;


import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.stats.server.exceptions.InvalidDateException;
import ru.practicum.ewm.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsService service;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid EndpointHit hit) {
        service.addHit(hit);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getStats(@RequestParam @NonNull String start,
                                                    @RequestParam @NonNull String end,
                                                    @RequestParam(required = false, defaultValue = "") List<String> uris,
                                                    @RequestParam(defaultValue = "false") Boolean unique) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        try {
            startDateTime = LocalDateTime.parse(start, FORMATTER);
            endDateTime = LocalDateTime.parse(end, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Invalid date format: " + e.getMessage());
        }

        List<ViewStats> results = service.getViews(startDateTime, endDateTime, uris, unique);
        return ResponseEntity.ok(results);
    }
}
