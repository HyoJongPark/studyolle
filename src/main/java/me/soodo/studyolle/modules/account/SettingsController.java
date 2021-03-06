package me.soodo.studyolle.modules.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.soodo.studyolle.modules.account.form.NicknameForm;
import me.soodo.studyolle.modules.account.form.Notifications;
import me.soodo.studyolle.modules.account.form.PasswordForm;
import me.soodo.studyolle.modules.account.form.Profile;
import me.soodo.studyolle.modules.account.validator.NicknameValidator;
import me.soodo.studyolle.modules.account.validator.PasswordFormValidator;
import me.soodo.studyolle.modules.tag.Tag;
import me.soodo.studyolle.modules.tag.form.TagForm;
import me.soodo.studyolle.modules.zone.Zone;
import me.soodo.studyolle.modules.tag.TagRepository;
import me.soodo.studyolle.modules.tag.TagService;
import me.soodo.studyolle.modules.zone.ZoneRepository;
import me.soodo.studyolle.modules.zone.form.ZoneForm;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    public static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    public static final String SETTINGS_PROFILE_URL = "/settings/profile";

    public static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    public static final String SETTINGS_PASSWORD_URL = "/settings/password";

    public static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    public static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

    public static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    public static final String SETTINGS_ACCOUNT_URL = "/settings/account";

    public static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    public static final String SETTINGS_TAGS_URL = "/settings/tags";

    public static final String SETTINGS_ZONES_VIEW_NAME = "settings/zones";
    public static final String SETTINGS_ZONES_URL = "/settings/zones";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        PasswordFormValidator passwordFormValidator = new PasswordFormValidator();
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile,
                                Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("passwordForm", new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm,
                                 Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "??????????????? ??????????????????.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String alarmUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("notifications", modelMapper.map(account, Notifications.class));
        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateAlarm(@CurrentUser Account account, @Valid Notifications notifications,
                                 Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }

        accountService.updateAlarm(account, notifications);
        attributes.addFlashAttribute("message", "????????? ??????????????????.");
        return "redirect:" + SETTINGS_NOTIFICATIONS_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String accountUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("nicknameForm", modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm,
                                 Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String tagsUpdateForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return SETTINGS_TAGS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_TAGS_URL + "/add")
    @ResponseBody
    public ResponseEntity updateTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagService.findOrCreateNew(tagForm);
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_TAGS_URL + "/remove")
    @ResponseBody
    public ResponseEntity deleteTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTINGS_ZONES_URL)
    public String zonesUpdateForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Zone> tags = accountService.getZones(account);
        model.addAttribute("zones", tags.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return SETTINGS_ZONES_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ZONES_URL + "/add")
    @ResponseBody
    public ResponseEntity updateZones(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_ZONES_URL + "/remove")
    @ResponseBody
    public ResponseEntity deleteZones(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }
}
