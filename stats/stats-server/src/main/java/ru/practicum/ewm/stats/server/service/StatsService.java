package ru.practicum.ewm.stats.server.service;

import jakarta.validation.Valid;
import lombok.NonNull;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.util.List;

public interface StatsService {
    void addHit(@Valid EndpointHit hit);
    List<ViewStats> getViews(@NonNull String start, @NonNull String end, List<String> uris, Boolean unique);
}
