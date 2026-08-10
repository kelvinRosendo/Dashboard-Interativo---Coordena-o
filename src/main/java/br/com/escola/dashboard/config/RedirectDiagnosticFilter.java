package br.com.escola.dashboard.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedirectDiagnosticFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("REDIRECT_DIAGNOSTIC");
    private static final String SESSION_KEY = "_redirect_chain";
    private static final int MAX_CHAIN_SIZE = 20;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String email = resolverEmail();
        HttpSession session = request.getSession(false);

        @SuppressWarnings("unchecked")
        List<String> chain = session != null
                ? (List<String>) session.getAttribute(SESSION_KEY)
                : null;

        if (chain == null) {
            chain = new ArrayList<>();
        }

        int repeticoes = contarRepeticoes(chain, uri);

        log.info("[REDIRECT] uri={} email={} chain_tamanho={} repeticoes_este_uri={}",
                uri, email, chain.size(), repeticoes);

        if (repeticoes >= 2) {
            log.error("[REDIRECT LOOP DETECTADO] uri={} email={} chain_completa={}",
                    uri, email, chain);
        }

        if (chain.size() >= MAX_CHAIN_SIZE) {
            log.error("[REDIRECT CHAIN MUITO LONGA] uri={} email={} chain_completa={}",
                    uri, email, chain);
        }

        chain.add(uri);
        if (chain.size() > MAX_CHAIN_SIZE) {
            chain = new ArrayList<>(chain.subList(chain.size() - MAX_CHAIN_SIZE, chain.size()));
        }

        if (session != null) {
            session.setAttribute(SESSION_KEY, chain);
        }

        int statusAnterior = response.getStatus();
        filterChain.doFilter(request, response);
        int statusAtual = response.getStatus();

        if (statusAtual == 302 || statusAtual == 301) {
            String location = response.getHeader("Location");
            log.info("[REDIRECT RESPONSE] {} → {} (status={})", uri, location, statusAtual);
        }

        if (statusAnterior == 200 && statusAtual == 302) {
            log.info("[REDIRECT CHAIN ATUAL] {}", chain);
        }
    }

    private int contarRepeticoes(List<String> chain, String uri) {
        int count = 0;
        for (String s : chain) {
            if (uri.equals(s)) {
                count++;
            }
        }
        return count;
    }

    private String resolverEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return "anonymous";
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            return email != null ? email : "no-email";
        }
        return auth.getName();
    }
}
