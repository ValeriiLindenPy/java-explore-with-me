package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void addHit(EndpointHit hit);

    List<ViewStats> getViews(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
