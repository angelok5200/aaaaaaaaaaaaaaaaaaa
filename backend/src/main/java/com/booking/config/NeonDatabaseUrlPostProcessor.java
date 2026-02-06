package com.booking.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Neon (and many hosting platforms) provide DATABASE_URL in the form:
 *   postgresql://user:password@host:5432/dbname?sslmode=require
 *
 * Spring's datasource expects a JDBC URL (jdbc:postgresql://...) and usually
 * separate username/password properties.
 *
 * This post-processor:
 *  - Detects non-JDBC postgres URLs in DATABASE_URL (or NEON_DATABASE_URL)
 *  - Converts them to a JDBC URL
 *  - Extracts username/password
 *  - Adds properties with high precedence so Spring Boot uses them.
 */
public class NeonDatabaseUrlPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public int getOrder() {
        // Run early, but after system properties.
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // If user already provided a JDBC url, do nothing.
        String existingUrl = firstNonEmpty(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("JDBC_DATABASE_URL"),
                environment.getProperty("SPRING_DATASOURCE_URL")
        );

        if (StringUtils.hasText(existingUrl) && existingUrl.startsWith("jdbc:")) {
            return;
        }

        String raw = firstNonEmpty(
                environment.getProperty("DATABASE_URL"),
                environment.getProperty("NEON_DATABASE_URL")
        );

        if (!StringUtils.hasText(raw)) {
            return;
        }

        if (raw.startsWith("jdbc:")) {
            return;
        }

        if (!(raw.startsWith("postgresql://") || raw.startsWith("postgres://"))) {
            return;
        }

        try {
            URI uri = URI.create(raw);

            String userInfo = uri.getUserInfo(); // user:pass
            String username = null;
            String password = null;
            if (StringUtils.hasText(userInfo)) {
                String[] parts = userInfo.split(":", 2);
                username = decode(parts[0]);
                if (parts.length > 1) password = decode(parts[1]);
            }

            String host = uri.getHost();
            int port = (uri.getPort() == -1) ? 5432 : uri.getPort();

            String path = uri.getPath();
            String database = (path != null && path.startsWith("/")) ? path.substring(1) : path;
            if (!StringUtils.hasText(database)) {
                // No database in URL; let Spring fail with a useful error.
                return;
            }

            Map<String, String> query = parseQuery(uri.getRawQuery());
            // Ensure SSL for Neon unless user explicitly overrides.
            query.putIfAbsent("sslmode", "require");

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            String qs = toQueryString(query);
            if (StringUtils.hasText(qs)) {
                jdbcUrl += "?" + qs;
            }

            Map<String, Object> props = new LinkedHashMap<>();
            props.put("spring.datasource.url", jdbcUrl);
            if (StringUtils.hasText(username) && !StringUtils.hasText(environment.getProperty("spring.datasource.username"))) {
                props.put("spring.datasource.username", username);
            }
            if (password != null && !StringUtils.hasText(environment.getProperty("spring.datasource.password"))) {
                props.put("spring.datasource.password", password);
            }

            environment.getPropertySources().addFirst(new MapPropertySource("neonDatabaseUrl", props));
        } catch (Exception ignored) {
            // If parsing fails, we don't crash the app here; Spring will fail later with config errors.
        }
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (StringUtils.hasText(v)) return v;
        }
        return null;
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new LinkedHashMap<>();
        if (!StringUtils.hasText(rawQuery)) return map;

        for (String pair : rawQuery.split("&")) {
            if (!StringUtils.hasText(pair)) continue;
            String[] kv = pair.split("=", 2);
            String k = decode(kv[0]);
            String v = kv.length > 1 ? decode(kv[1]) : "";
            if (StringUtils.hasText(k)) {
                map.put(k, v);
            }
        }
        return map;
    }

    private static String toQueryString(Map<String, String> query) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e : query.entrySet()) {
            if (!StringUtils.hasText(e.getKey())) continue;
            if (!first) sb.append("&");
            first = false;
            sb.append(encode(e.getKey()));
            sb.append("=");
            sb.append(encode(e.getValue()));
        }
        return sb.toString();
    }

    private static String decode(String s) {
        if (s == null) return null;
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static String encode(String s) {
        if (s == null) return "";
        // Minimal safe encoding for query string; space as %20.
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
