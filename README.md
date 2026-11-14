# Offer Letter Backend — Appraisal datasource pattern

This backend has been updated to follow the database configuration approach used by the supplied Appraisal backend.

## Important change

The Offer Letter backend previously created its own `DataSource` bean in `DataSourceConfig`. That custom bean bypassed the standard Spring datasource normalization approach used by Appraisal.

This package removes that custom `DataSourceConfig` and adds the Appraisal-style `DatasourceEnvironmentPostProcessor`. Spring Boot's standard Hikari datasource auto-configuration now reads:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

The post-processor:
- Accepts a `mysql://user:password@host:port/database?...` Aiven URL and converts it to JDBC.
- Extracts credentials from a URL if separate username/password variables are not set.
- Normalizes quotes/whitespace.
- Converts `ssl-mode` to Connector/J `sslMode`.
- Adds `sslMode=REQUIRED` for remote MySQL URLs if no SSL option is present.
- Fails clearly if Render is configured with localhost.

## Render environment

In Render → Offer Letter backend Web Service → Environment, set these exact names:

| Variable | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | Full Aiven JDBC URL or Aiven `mysql://...` connection URL |
| `SPRING_DATASOURCE_USERNAME` | Aiven MySQL username |
| `SPRING_DATASOURCE_PASSWORD` | Aiven MySQL password |
| `JPA_DDL_AUTO` | `update` |
| `APP_ALLOWED_ORIGINS` | `http://localhost:5173,https://YOUR-VERCEL-DOMAIN` |
| `APP_PUBLIC_BASE_URL` | `https://YOUR-RENDER-BACKEND.onrender.com` |
| `MAIL_USERNAME` | Gmail address |
| `MAIL_PASSWORD` | New Google App Password |
| `MAIL_FROM` | Gmail address |

For the URL, best practice is to copy the JDBC URL from Aiven's Connection Information. It should look like:

`jdbc:mysql://YOUR-HOST:YOUR-PORT/YOUR-DATABASE?sslMode=REQUIRED`

If using Aiven's `mysql://username:password@host:port/database?ssl-mode=REQUIRED` URI, the included processor converts it. Do not add quotes around values.

## Aiven checklist

1. Confirm the MySQL service is Running.
2. Copy the hostname, port, database name and username from the correct Aiven service.
3. Ensure public access is enabled if Render connects over the internet.
4. Ensure Aiven's IP allowlist/firewall permits Render's outbound traffic. Use the current outbound IP information for your Render service/region; do not guess.
5. Confirm the user has privileges on the database.

## Deploy

1. Replace the Offer Letter backend source with this package (keep the folder structure).
2. Commit and push to the configured GitHub repository.
3. In Render, confirm the Environment variables above are set.
4. Select Manual Deploy → Deploy latest commit.
5. Inspect the *new deployment's* logs. If the same `Communications link failure` persists, the URL or database network access is still wrong; Java code cannot repair an unreachable host or blocked port.

## Local run (Windows CMD)

```cmd
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/offerletter_db
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD
mvn clean spring-boot:run
```

## Email and PDF notes

- Gmail SMTP remains implemented using Spring JavaMailSender. Render Free may block outbound SMTP port 587; this is separate from the MySQL startup failure.
- Render local filesystem is ephemeral unless a persistent disk is attached. Uploaded files under `uploads/` may not survive restarts/redeploys.

## Build

```cmd
mvn clean package
```

This ZIP excludes `.git` history, build output, and hardcoded credentials. The React/Vite frontend is not included.
