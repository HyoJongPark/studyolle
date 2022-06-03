package me.soodo.studyolle.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
