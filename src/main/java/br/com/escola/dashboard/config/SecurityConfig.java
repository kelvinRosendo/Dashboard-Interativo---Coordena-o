package br.com.escola.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/tv/**",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .requestMatchers(
                                "/admin",
                                "/admin/**",
                                "/agenda/**",
                                "/novo-card",
                                "/salvar-card",
                                "/editar-card/**",
                                "/atualizar-card/**",
                                "/deletar-card/**",
                                "/cards/**",
                                "/relatorio",
                                "/relatorio/**",
                                "/coordenadoras",
                                "/coordenadoras/**",
                                "/admin/importacao",
                                "/admin/importacao/**",
                                "/admin/usuarios",
                                "/admin/usuarios/**",
                                "/dashboard",
                                "/dashboard/**",
                                "/vice-diretora",
                                "/vice-diretora/**",
                                "/coordenadora/dashboard",
                                "/coordenadora/dashboard/**"
                        ).authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/dashboard", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
