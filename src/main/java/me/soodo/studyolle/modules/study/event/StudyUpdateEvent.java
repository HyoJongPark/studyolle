package me.soodo.studyolle.modules.study.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.soodo.studyolle.modules.study.Study;

@Getter
@RequiredArgsConstructor
public class StudyUpdateEvent {

    private final Study study;

    private final String message;

}
