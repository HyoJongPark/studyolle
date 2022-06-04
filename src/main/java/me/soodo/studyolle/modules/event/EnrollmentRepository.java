package me.soodo.studyolle.modules.event;

import me.soodo.studyolle.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByAccountAndEvent(Account account, Event event);

    Enrollment findByAccountAndEvent(Account account, Event event);
}
