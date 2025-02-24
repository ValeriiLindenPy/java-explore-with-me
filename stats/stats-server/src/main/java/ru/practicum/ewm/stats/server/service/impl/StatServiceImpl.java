package ru.practicum.ewm.stats.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.stats.server.repository.EndpointHitRepository;
import ru.practicum.ewm.stats.server.repository.ViewStatsProjection;
import ru.practicum.ewm.stats.server.service.HitMapper;
import ru.practicum.ewm.stats.server.service.StatsService;

import java.time.LocalDateTime;
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
    public List<ViewStats> getViews(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<String> processedUris = (uris != null) ? uris : List.of();

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (unique) {
            return processedUris.isEmpty()
                    ? getUniqueStats(start, end)
                    : getUniqueStatsByUris(start, end, processedUris);
        } else {
            return processedUris.isEmpty()
                    ? getAllStats(start, end)
                    : getStatsByUris(start, end, processedUris);
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