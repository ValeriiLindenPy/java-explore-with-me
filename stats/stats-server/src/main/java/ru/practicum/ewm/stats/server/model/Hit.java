package ru.practicum.ewm.stats.server.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
@Builder
@Table(name = "hit")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
