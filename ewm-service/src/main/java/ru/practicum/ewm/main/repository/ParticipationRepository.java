package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.Participation;
import ru.practicum.ewm.main.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByEvent_IdAndRequester_Id(Long eventId, Long userId);

    List<Participation> findByIdInAndEventId(List<Long> requestIds, Long eventId);

    List<Participation> findByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    List<Participation> findByRequesterId(Long userId);

    Optional<Participation> findByIdAndRequesterId(Long requestId, Long userId);

    List<Participation> findByEvent_Id(Long eventId);
}
