package me.soodo.studyolle.modules.notification;

import lombok.*;
import me.soodo.studyolle.modules.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private Account account;

    private LocalDateTime createdDateTime;

    @Enumerated
    private NotificationType notificationType;
}
