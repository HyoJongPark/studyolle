package me.soodo.studyolle.modules.study.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.soodo.studyolle.infra.config.AppProperties;
import me.soodo.studyolle.modules.account.Account;
import me.soodo.studyolle.modules.account.AccountPredicates;
import me.soodo.studyolle.modules.account.AccountRepository;
import me.soodo.studyolle.modules.mail.EmailMessage;
import me.soodo.studyolle.modules.mail.EmailService;
import me.soodo.studyolle.modules.notification.Notification;
import me.soodo.studyolle.modules.notification.NotificationRepository;
import me.soodo.studyolle.modules.notification.NotificationType;
import me.soodo.studyolle.modules.study.Study;
import me.soodo.studyolle.modules.study.StudyRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final EmailService emailService;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                sendStudyCreatedEmail(study, account, "새로운 스터디가 생성되었습니다.", "스터디가 생성되었습니다.");
            }

            if (account.isStudyCreatedByWeb()) {
                createNotification(study, account, study.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        });
    }

    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        accounts.forEach(account -> {
            if (account.isStudyUpdatedByEmail()) {
                sendStudyCreatedEmail(study, account, studyUpdateEvent.getMessage(), "스터디에 새소식이 있습니다.");
            }

            if (account.isStudyUpdatedByWeb()) {
                createNotification(study, account, studyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED);
            }
        });

    }

    private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendStudyCreatedEmail(Study study, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디올래, '" + study.getTitle() + "' " + emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}

