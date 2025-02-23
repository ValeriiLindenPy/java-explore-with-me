package ru.practicum.ewm.stats.server.repository;

public interface ViewStatsProjection {
    String getApp();
    String getUri();
    Long getHits();
}
