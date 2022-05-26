package me.soodo.studyolle.settings;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.soodo.studyolle.domain.Account;

@Data
@NoArgsConstructor
public class Profile {

    private String bio;
    private String url;
    private String occupation;
    private String location;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
