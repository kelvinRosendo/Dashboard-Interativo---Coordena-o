package br.com.escola.dashboard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AdminAuthService {

    private final String authorizedEmailsConfig;

    public AdminAuthService(@Value("${app.admin.authorized-emails}") String authorizedEmailsConfig) {
        this.authorizedEmailsConfig = authorizedEmailsConfig;
    }

    public boolean isAdminEmailAuthorized(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        Set<String> authorizedEmails = new HashSet<>();
        if (authorizedEmailsConfig != null && !authorizedEmailsConfig.isBlank()) {
            String[] emails = authorizedEmailsConfig.split(",");
            for (String authorizedEmail : emails) {
                authorizedEmails.add(authorizedEmail.trim().toLowerCase());
            }
        }

        return authorizedEmails.contains(email.trim().toLowerCase());
    }
}
