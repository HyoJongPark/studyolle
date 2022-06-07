package me.soodo.studyolle.infra.main;

import lombok.RequiredArgsConstructor;
import me.soodo.studyolle.modules.account.CurrentUser;
import me.soodo.studyolle.modules.account.Account;
import me.soodo.studyolle.modules.notification.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        long count = notificationRepository.countByAccountAndChecked(account, false);
        model.addAttribute("hasNotification", count > 0);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "/login";
    }
}
