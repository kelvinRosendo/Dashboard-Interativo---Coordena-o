package br.com.escola.dashboard.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminAccessDiagnosticsFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccessDiagnosticsFilter.class);

    private final String adminEmails;

    public AdminAccessDiagnosticsFilter(@Value("${ADMIN_EMAILS:}") String adminEmails) {
        this.adminEmails = adminEmails;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isAdminRequest(request)) {
            String email = normalizarEmail(resolverEmailAutenticado());
            Set<String> admins = normalizarAdminEmails();
            boolean authorized = admins.isEmpty() || admins.contains(email);

            logger.info(
                    "Admin access check: email={} authorized={} adminEmails={}",
                    email,
                    authorized,
                    admins
            );
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAdminRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        return path.equals("/admin") || path.startsWith("/admin/");
    }

    private String resolverEmailAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email != null && !email.isBlank()) {
                return email;
            }
        }

        return authentication.getName();
    }

    private Set<String> normalizarAdminEmails() {
        if (adminEmails == null || adminEmails.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(adminEmails.split(","))
                .map(this::normalizarEmail)
                .filter(email -> !email.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
