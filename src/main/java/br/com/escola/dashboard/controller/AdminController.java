package br.com.escola.dashboard.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String painelAdministrativo(@AuthenticationPrincipal OAuth2User usuario, Model model) {
        String nome = usuario != null ? usuario.getAttribute("name") : "Usuario";
        String email = usuario != null ? usuario.getAttribute("email") : null;

        model.addAttribute("nome", nome);
        model.addAttribute("email", email);

        return "admin";
    }
}
