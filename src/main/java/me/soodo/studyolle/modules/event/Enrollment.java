package me.soodo.studyolle.modules.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.soodo.studyolle.modules.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    private LocalDateTime enrolledAt;

    private boolean accepted = false;

    private boolean attended;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

}
