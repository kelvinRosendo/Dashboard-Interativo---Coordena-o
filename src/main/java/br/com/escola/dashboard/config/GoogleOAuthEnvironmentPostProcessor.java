package br.com.escola.dashboard.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleOAuthEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "googleOAuthResolvedProperties";
    private static final String GOOGLE_CLIENT_ID = "GOOGLE_CLIENT_ID";
    private static final String GOOGLE_CLIENT_SECRET = "GOOGLE_CLIENT_SECRET";
    private static final String SPRING_CLIENT_ID = "spring.security.oauth2.client.registration.google.client-id";
    private static final String SPRING_CLIENT_SECRET = "spring.security.oauth2.client.registration.google.client-secret";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, String> envFile = carregarEnvLocal();

        String clientId = primeiroValorValido(
                System.getenv(GOOGLE_CLIENT_ID),
                envFile.get(GOOGLE_CLIENT_ID),
                envFile.get(SPRING_CLIENT_ID),
                environment.getProperty(SPRING_CLIENT_ID)
        );

        String clientSecret = primeiroValorValido(
                System.getenv(GOOGLE_CLIENT_SECRET),
                envFile.get(GOOGLE_CLIENT_SECRET),
                envFile.get(SPRING_CLIENT_SECRET),
                environment.getProperty(SPRING_CLIENT_SECRET)
        );

        Map<String, Object> propriedades = new HashMap<>();
        if (temTexto(clientId)) {
            propriedades.put(SPRING_CLIENT_ID, clientId);
        }
        if (temTexto(clientSecret)) {
            propriedades.put(SPRING_CLIENT_SECRET, clientSecret);
        }

        if (!propriedades.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, propriedades));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Map<String, String> carregarEnvLocal() {
        Path envPath = Path.of(System.getProperty("user.dir"), ".env");
        if (!Files.isRegularFile(envPath)) {
            return Map.of();
        }

        try {
            List<String> linhas = Files.readAllLines(envPath);
            Map<String, String> valores = new HashMap<>();
            for (String linha : linhas) {
                String texto = linha.trim();
                if (texto.isBlank() || texto.startsWith("#") || !texto.contains("=")) {
                    continue;
                }

                String[] partes = texto.split("=", 2);
                valores.put(partes[0].trim(), limparValor(partes[1]));
            }
            return valores;
        } catch (IOException ex) {
            return Map.of();
        }
    }

    private String primeiroValorValido(String... valores) {
        for (String valor : valores) {
            String texto = limparValor(valor);
            if (temTexto(texto) && !texto.startsWith("configure-")) {
                return texto;
            }
        }
        return null;
    }

    private String limparValor(String valor) {
        if (valor == null) {
            return null;
        }

        String texto = valor.trim();
        if (texto.length() >= 2 && texto.startsWith("\"") && texto.endsWith("\"")) {
            return texto.substring(1, texto.length() - 1).trim();
        }
        return texto;
    }

    private boolean temTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
