package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
