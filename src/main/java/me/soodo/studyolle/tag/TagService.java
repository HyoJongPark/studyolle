package me.soodo.studyolle.tag;

import lombok.RequiredArgsConstructor;
import me.soodo.studyolle.domain.Tag;
import me.soodo.studyolle.settings.TagForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateNew(TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(title).build());
        }

        return tag;
    }
}
