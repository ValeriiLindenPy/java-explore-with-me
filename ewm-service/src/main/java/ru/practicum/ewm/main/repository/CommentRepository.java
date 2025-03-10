package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByEventIdAndIsModeratedTrue(Long eventId, Pageable pageable);
}
