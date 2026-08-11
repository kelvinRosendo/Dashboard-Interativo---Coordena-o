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
    private static final int MAX_CHAIN_SIZE = 15;
    private static final int LOOP_THRESHOLD = 3;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (isStaticResource(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);

        int status = response.getStatus();

        if (status == 301 || status == 302) {
            String location = response.getHeader("Location");
            String email = resolverEmail();

            HttpSession session = request.getSession(true);
            @SuppressWarnings("unchecked")
            List<String> chain = (List<String>) session.getAttribute(SESSION_KEY);
            if (chain == null) {
                chain = new ArrayList<>();
            }

            chain.add(uri);
            if (chain.size() > MAX_CHAIN_SIZE) {
                chain = new ArrayList<>(chain.subList(chain.size() - MAX_CHAIN_SIZE, chain.size()));
            }
            session.setAttribute(SESSION_KEY, chain);

            log.debug("[REDIRECT] {} -> {} (status={})", uri, location, status);

            int repeticoes = contarRepeticoes(chain, uri);
            if (repeticoes >= LOOP_THRESHOLD) {
                log.warn("[POSSIVEL REDIRECT LOOP] uri={} email={} chain={}", uri, email, chain);
            }
        } else if (status == 200) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(SESSION_KEY);
            }
        }
    }

    private boolean isStaticResource(String uri) {
        return uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/img/")
                || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png")
                || uri.endsWith(".jpg") || uri.endsWith(".ico") || uri.endsWith(".svg")
                || uri.endsWith(".woff") || uri.endsWith(".woff2");
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
