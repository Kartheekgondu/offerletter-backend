package com.uxinterfacely.offerletter.config;

import java.net.URI;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${app.db.url}") String rawUrl,
            @Value("${app.db.username:}") String rawUser,
            @Value("${app.db.password:}") String rawPassword) {

        String url = clean(rawUrl);
        String user = clean(rawUser);
        String password = stripQuotes(rawPassword == null ? "" : rawPassword.trim());

        if (url.isEmpty()) {
            throw new IllegalStateException("DB_URL is missing. Set the full MySQL JDBC URL in Render Environment.");
        }

        if (url.regionMatches(true, 0, "mysql://", 0, 8)) {
            try {
                URI uri = URI.create(url);
                if (uri.getHost() == null || uri.getHost().isBlank()) {
                    throw new IllegalArgumentException("DB_URL has no database host");
                }
                if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                    String[] credentials = uri.getUserInfo().split(":", 2);
                    if (user.isBlank()) user = credentials[0];
                    if (password.isBlank() && credentials.length > 1) password = credentials[1];
                }
                StringBuilder jdbc = new StringBuilder("jdbc:mysql://").append(uri.getHost());
                if (uri.getPort() > 0) jdbc.append(':').append(uri.getPort());
                if (uri.getRawPath() != null) jdbc.append(uri.getRawPath());
                if (uri.getRawQuery() != null) jdbc.append('?').append(uri.getRawQuery());
                url = jdbc.toString();
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException("DB_URL is not a valid mysql:// URL. Prefer the JDBC URL from your database provider.", ex);
            }
        }

        if (!url.regionMatches(true, 0, "jdbc:mysql://", 0, 13)) {
            throw new IllegalStateException("DB_URL must start with jdbc:mysql:// (copy the JDBC URL from Aiven).");
        }
        if (user.isBlank()) {
            throw new IllegalStateException("DB_USERNAME is missing. Set the database username in Render Environment.");
        }
        if (password.isBlank()) {
            throw new IllegalStateException("DB_PASSWORD is missing. Set the database password in Render Environment.");
        }

        log.info("Configuring MySQL connection to {}", maskUrl(url));

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(password);
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setPoolName("offerletter-pool");
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(0);
        cfg.setConnectionTimeout(30000);
        cfg.setValidationTimeout(10000);
        cfg.setInitializationFailTimeout(30000);
        cfg.setMaxLifetime(1500000);
        return new HikariDataSource(cfg);
    }

    private static String clean(String value) {
        return value == null ? "" : stripQuotes(value.trim());
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2 &&
                ((value.startsWith("\"") && value.endsWith("\"")) ||
                 (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static String maskUrl(String value) {
        return value.replaceAll("//[^/@]*@", "//***@");
    }
}
