package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.stats.server.model.Hit;

public class HitMapper {
    public static Hit toHit(EndpointHit endpointHit) {
        return Hit.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .uri(endpointHit.getUri())
                .build();
    }
}
