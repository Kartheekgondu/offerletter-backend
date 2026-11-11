package com.uxinterfacely.offerletter.config;

import java.net.URI;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Builds the MySQL DataSource from DB_URL / DB_USERNAME / DB_PASSWORD.
 *
 * Fixes the Render error
 *   "Unable to determine Dialect without JDBC metadata"
 * which Hibernate throws whenever the database connection cannot be opened.
 * The usual causes are handled here:
 *   - URL given as  mysql://...  instead of  jdbc:mysql://...
 *   - URL in the form  mysql://user:password@host:port/db
 *   - stray spaces / quotes copied into the Render environment value
 */
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
        String password = rawPassword == null ? "" : stripQuotes(rawPassword);

        if (url.isEmpty()) {
            throw new IllegalStateException(
                    "DB_URL is empty. Set DB_URL in Render, e.g. jdbc:mysql://HOST:3306/DBNAME?sslMode=REQUIRED");
        }

        // Accept  mysql://user:pass@host:port/db?params  and add the jdbc: prefix.
        if (!url.regionMatches(true, 0, "jdbc:", 0, 5)) {
            if (!url.regionMatches(true, 0, "mysql://", 0, 8)) {
                throw new IllegalStateException(
                        "DB_URL must start with jdbc:mysql:// (current value starts with '"
                                + url.substring(0, Math.min(url.length(), 12)) + "')");
            }
            try {
                URI uri = URI.create(url);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && !userInfo.isEmpty()) {
                    int colon = userInfo.indexOf(':');
                    String uriUser = colon >= 0 ? userInfo.substring(0, colon) : userInfo;
                    String uriPass = colon >= 0 ? userInfo.substring(colon + 1) : "";
                    if (user.isEmpty()) user = uriUser;
                    if (password.isEmpty()) password = uriPass;
                }
                StringBuilder sb = new StringBuilder("jdbc:mysql://").append(uri.getHost());
                if (uri.getPort() > 0) sb.append(':').append(uri.getPort());
                sb.append(uri.getRawPath() == null ? "" : uri.getRawPath());
                if (uri.getRawQuery() != null) sb.append('?').append(uri.getRawQuery());
                url = sb.toString();
            } catch (IllegalArgumentException ex) {
                // e.g. password with special characters inside the URL
                url = "jdbc:" + url;
            }
        }

        if (user.isEmpty()) user = "root";

        log.info("Database target: {} (user: {})", maskUrl(url), user);

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(password);
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setPoolName("offerletter-pool");
        cfg.setMaximumPoolSize(5);          // small pool: Render free tier + hosted MySQL limits
        cfg.setMinimumIdle(1);
        cfg.setConnectionTimeout(30_000);   // allow slow / waking databases
        cfg.setMaxLifetime(25 * 60 * 1000L); // recycle before the DB host drops idle connections
        return new HikariDataSource(cfg);
    }

    private static String clean(String v) {
        return v == null ? "" : stripQuotes(v.trim());
    }

    private static String stripQuotes(String v) {
        String s = v;
        if (s.length() >= 2
                && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    /** Remove any password that might be embedded in the URL before logging. */
    private static String maskUrl(String url) {
        return url.replaceAll("//[^/@]*@", "//***@");
    }
}
