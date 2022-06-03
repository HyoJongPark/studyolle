package me.soodo.studyolle.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.soodo.studyolle.domain.Enrollment;

@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {

    protected final Enrollment enrollment;

    protected final String message;

}
