package ru.practicum.stats.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.stats.client.StatsClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {
    private final RestTemplate rest;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public ResponseEntity<Void> postHit(String app, String uri, String ip) {
        EndpointHit hit = EndpointHit.builder()
                .app(app)
                .timestamp(LocalDateTime.now())
                .uri(uri)
                .ip(ip)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHit> request = new HttpEntity<>(hit, headers);

        return rest.exchange("/hit", HttpMethod.POST, request, Void.class);
    }

    @Override
    public ResponseEntity<Object> getViewStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime parsedStart = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime parsedEnd = LocalDateTime.parse(end, FORMATTER);

        String encodedStart = URLEncoder.encode(FORMATTER.format(parsedStart), StandardCharsets.UTF_8);
        String encodedEnd = URLEncoder.encode(FORMATTER.format(parsedEnd), StandardCharsets.UTF_8);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", encodedStart)
                .queryParam("end", encodedEnd);

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
        }

        if (unique != null) {
            builder.queryParam("unique", unique);
        }

        return rest.exchange(builder.toUriString(), HttpMethod.GET, null, Object.class);
    }
}
