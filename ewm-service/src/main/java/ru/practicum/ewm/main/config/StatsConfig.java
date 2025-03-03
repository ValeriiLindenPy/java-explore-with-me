package ru.practicum.ewm.main.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.client.impl.StatsClientImpl;

@Configuration
@RequiredArgsConstructor
public class StatsConfig {
    private final RestTemplate restTemplate;

    @Bean
    public StatsClient statsClient() {
        return new StatsClientImpl(restTemplate);
    }
}
