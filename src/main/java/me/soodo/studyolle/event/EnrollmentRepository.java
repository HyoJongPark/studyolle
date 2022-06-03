package me.soodo.studyolle.event;

import me.soodo.studyolle.domain.Account;
import me.soodo.studyolle.domain.Enrollment;
import me.soodo.studyolle.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByAccountAndEvent(Account account, Event event);

    Enrollment findByAccountAndEvent(Account account, Event event);
}
