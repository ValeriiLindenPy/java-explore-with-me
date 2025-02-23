package ru.practicum.stats.client;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StatsClient {

    ResponseEntity<Void> postHit(@NotBlank String app, @NotBlank String uri, @NotBlank String ip);

    ResponseEntity<Object> getViewStats(@NonNull String start, @NonNull String end, @Nullable List<String> uris, @Nullable Boolean unique);

}
