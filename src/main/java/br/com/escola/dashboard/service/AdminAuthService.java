package br.com.escola.dashboard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminAuthService {

    private final Set<String> authorizedEmails;

    public AdminAuthService(@Value("${app.admin.authorized-emails}") String authorizedEmailsConfig) {
        this.authorizedEmails = parseEmails(authorizedEmailsConfig);
    }

    public boolean isAdminEmailAuthorized(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return authorizedEmails.contains(email.trim().toLowerCase());
    }

    public boolean isAdmin(OAuth2User usuario) {
        if (usuario == null) {
            return false;
        }
        return isAdminEmailAuthorized(usuario.getAttribute("email"));
    }

    private Set<String> parseEmails(String config) {
        if (config == null || config.isBlank()) {
            return Set.of();
        }
        return Set.of(java.util.Arrays.stream(config.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toArray(String[]::new));
    }
}
