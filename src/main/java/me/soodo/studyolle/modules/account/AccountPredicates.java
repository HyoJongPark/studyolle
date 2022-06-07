package me.soodo.studyolle.modules.account;

import com.querydsl.core.types.Predicate;
import me.soodo.studyolle.modules.tag.Tag;
import me.soodo.studyolle.modules.zone.Zone;

import java.util.Set;

import static me.soodo.studyolle.modules.account.QAccount.*;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
