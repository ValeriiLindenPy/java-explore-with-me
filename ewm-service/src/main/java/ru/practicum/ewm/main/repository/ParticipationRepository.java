package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.Participation;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
}
