package me.soodo.studyolle.event;

import me.soodo.studyolle.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
