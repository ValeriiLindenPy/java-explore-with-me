package ru.practicum.stats.client;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StatsClient {

    ResponseEntity<Void> postHit(@NotBlank String app, @NotBlank String uri, @NotBlank String ip);

    ResponseEntity<Object> getViewStats(@NotNull String start, @NotNull String end, @Nullable List<String> uris, @Nullable Boolean unique);
}