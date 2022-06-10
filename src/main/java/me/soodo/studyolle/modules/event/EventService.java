package me.soodo.studyolle.modules.event;

import lombok.RequiredArgsConstructor;
import me.soodo.studyolle.modules.account.Account;
import me.soodo.studyolle.modules.event.event.EnrollmentAcceptedEvent;
import me.soodo.studyolle.modules.event.event.EnrollmentRejectedEvent;
import me.soodo.studyolle.modules.event.form.EventForm;
import me.soodo.studyolle.modules.study.Study;
import me.soodo.studyolle.modules.study.event.StudyUpdateEvent;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "모임이 생성되었습니다."));

        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingList();
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "모임이 수되었습니다."));
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "모임이 취소되었습니다."));
    }

    public void newEnrollment(Account account, Event event) {
        if (!enrollmentRepository.existsByAccountAndEvent(account, event)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setAccount(account);
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());

            enrollment.setEvent(event);
            event.getEnrollments().add(enrollment);

            enrollmentRepository.save(enrollment);
        }
    }

    public void cancelEnrollment(Account account, Event event) {
        Enrollment enrollment = enrollmentRepository.findByAccountAndEvent(account, event);
        event.removeEnrollment(enrollment);
        enrollmentRepository.delete(enrollment);

        event.acceptNextEnrollment();
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }
}
