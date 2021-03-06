package me.soodo.studyolle.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.soodo.studyolle.modules.account.AccountRepository;
import me.soodo.studyolle.modules.account.AccountService;
import me.soodo.studyolle.modules.account.form.SignUpForm;
import me.soodo.studyolle.modules.account.Account;
import me.soodo.studyolle.modules.account.SettingsController;
import me.soodo.studyolle.modules.tag.form.TagForm;
import me.soodo.studyolle.modules.zone.form.ZoneForm;
import me.soodo.studyolle.modules.tag.Tag;
import me.soodo.studyolle.modules.zone.Zone;
import me.soodo.studyolle.modules.tag.TagRepository;
import me.soodo.studyolle.modules.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired TagRepository tagRepository;
    @Autowired ZoneRepository zoneRepository;
    @Autowired ObjectMapper objectMapper;

    private Zone testZone = Zone.builder().city("test").localNameOfCity("????????????").province("????????????").build();

    @BeforeEach
    void before() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("park");
        signUpForm.setEmail("email@email.com");
        signUpForm.setPassword("87654321");
        accountService.processNewAccount(signUpForm);
        zoneRepository.save(testZone);
    }

    @AfterEach
    void after() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ???")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateProfile() throws Exception {
        String bio = "?????? ????????? ???????????? ??????.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("park");
        assertEquals(bio, account.getBio());
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "?????? ????????? ???????????? ??????. ???????????? ????????? ?????? ????????? ????????????????????????? ????????? ???????????? ??????. ???????????? ????????? ?????? ????????? ???????????????????";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("park");
        assertNull(account.getBio());
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? ???")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }


    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ???????????? - ????????? ??????")
    @Test
    void updatePassword() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "12345678";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPasswordConfirm)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("park");
        assertTrue(passwordEncoder.matches(newPassword, account.getPassword()));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ???????????? - ????????? ??????")
    @Test
    void updatePassword_error() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "********";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPasswordConfirm)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("park");
        assertFalse(passwordEncoder.matches(newPassword, account.getPassword()));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ???")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ??????")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag findTag = tagRepository.findByTitle("newTag");
        assertNotNull(findTag);
        assertTrue(accountRepository.findByNickname("park").getTags().contains(findTag));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ??????")
    @Test
    void deleteTag() throws Exception {
        Account account = accountRepository.findByNickname("park");
        Tag tag = tagRepository.save(Tag.builder().title("new Tag").build());
        accountService.addTag(account, tag);

        assertTrue(account.getTags().contains(tag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("new Tag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(account.getTags().contains(tag));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ???")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ZONES_URL))
                .andExpect(view().name(SettingsController.SETTINGS_ZONES_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ??????")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(SettingsController.SETTINGS_ZONES_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNickname("park");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(account.getZones().contains(zone));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ??????")
    @Test
    void removeZone() throws Exception {
        Account account = accountRepository.findByNickname("park");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(account, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(SettingsController.SETTINGS_ZONES_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(account.getZones().contains(zone));
    }
}