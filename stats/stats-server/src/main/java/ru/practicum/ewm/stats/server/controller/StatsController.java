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
import ru.practicum.ewm.stats.server.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid EndpointHit hit) {
        service.addHit(hit);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getStats(@RequestParam @NonNull String start,
                                                    @RequestParam @NonNull String end,
                                                    @RequestParam(required = false, defaultValue = "") List<String> uris,
                                                    @RequestParam(defaultValue = "false") Boolean unique ) {
        List<ViewStats> results = service.getViews(start, end, uris, unique);
        return ResponseEntity.ok(results);
    }
}
