package me.soodo.studyolle.event;

import lombok.RequiredArgsConstructor;
import me.soodo.studyolle.domain.Account;
import me.soodo.studyolle.domain.Event;
import me.soodo.studyolle.domain.Study;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreateBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);

        return eventRepository.save(event);
    }
}
