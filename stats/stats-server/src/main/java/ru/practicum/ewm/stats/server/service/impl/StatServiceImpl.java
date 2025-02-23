package ru.practicum.ewm.stats.server.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.stats.server.repository.EndpointHitRepository;
import ru.practicum.ewm.stats.server.repository.ViewStatsProjection;
import ru.practicum.ewm.stats.server.service.HitMapper;
import ru.practicum.ewm.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatsService {
    private final EndpointHitRepository repository;

    @Override
    public void addHit(EndpointHit hit) {
        repository.save(HitMapper.toHit(hit));
    }

    @Override
    public List<ViewStats> getViews(@NonNull String start, @NonNull String end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
        List<String> processedUris = (uris != null) ? uris : List.of();

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (unique) {
            return processedUris.isEmpty()
                    ? getUniqueStats(startDateTime, endDateTime)
                    : getUniqueStatsByUris(startDateTime, endDateTime, processedUris);
        } else {
            return processedUris.isEmpty()
                    ? getAllStats(startDateTime, endDateTime)
                    : getStatsByUris(startDateTime, endDateTime, processedUris);
        }
    }

    private List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end) {
        return convertProjections(repository.getAllViewStatsProjection(start, end));
    }

    private List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return convertProjections(repository.getViewStatsProjectionByUris(start, end, uris));
    }

    private List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end) {
        return convertProjections(repository.getViewStatsProjectionUnique(start, end));
    }

    private List<ViewStats> getUniqueStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return convertProjections(repository.getViewStatsProjectionByUrisUnique(start, end, uris));
    }

    private List<ViewStats> convertProjections(List<ViewStatsProjection> projections) {
        return projections.stream()
                .map(proj -> ViewStats.builder()
                        .app(proj.getApp())
                        .uri(proj.getUri())
                        .hits(proj.getHits())
                        .build())
                .toList();
    }
}
