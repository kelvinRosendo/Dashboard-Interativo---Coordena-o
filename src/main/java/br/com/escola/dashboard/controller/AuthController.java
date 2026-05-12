package br.com.escola.dashboard.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal OAuth2User usuario, Model model) {
        if (usuario != null) {
            return "redirect:/admin";
        }

        model.addAttribute("googleConfigurado", googleConfigurado());
        return "login";
    }

    private boolean googleConfigurado() {
        return googleClientId != null
                && !googleClientId.isBlank()
                && !googleClientId.startsWith("configure-");
    }
}
