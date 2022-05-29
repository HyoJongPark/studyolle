package me.soodo.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.soodo.studyolle.account.AccountRepository;
import me.soodo.studyolle.account.AccountService;
import me.soodo.studyolle.account.SignUpForm;
import me.soodo.studyolle.domain.Account;
import me.soodo.studyolle.domain.Tag;
import me.soodo.studyolle.domain.Zone;
import me.soodo.studyolle.tag.TagRepository;
import me.soodo.studyolle.zone.ZoneRepository;
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

    private Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

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
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
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
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 글자수에 제한이 있기 때문에 큰일나겠지요?길게 소개를 수정하는 경우. 글자수에 제한이 있기 때문에 큰일나겠지요?";
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
    @DisplayName("비밀번호 수정 폼")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }


    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 수정하기 - 입력값 정상")
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
    @DisplayName("비밀번호 수정하기 - 입력값 에러")
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
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("태그 추가")
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
    @DisplayName("태그 삭제")
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
    @DisplayName("지역 정보 폼")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ZONES_URL))
                .andExpect(view().name(SettingsController.SETTINGS_ZONES_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithUserDetails(value = "park", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지역 정보 추가")
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
    @DisplayName("지역 정보 삭제")
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