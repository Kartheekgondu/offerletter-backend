# Offer Letter Backend — Appraisal-style datasource + CORS fix

This is the complete Offer Letter backend source. The database environment processor follows the uploaded Appraisal backend's `DatasourceEnvironmentPostProcessor` pattern. The Offer Letter controllers, entities, repository, services, PDF upload and Gmail SMTP implementation are retained.

## Important diagnosis

The browser screenshot says `Failed to fetch`, but the Render logs show Spring Boot fails during startup with MySQL `Communications link failure`. When the backend process is down, the browser cannot reach it and may display a fetch/CORS-style message. Fixing CORS alone cannot fix a backend that never starts.

The CORS filter is configured for `/api/**`, allows GET/POST/PUT/PATCH/DELETE/OPTIONS and accepts the configured frontend origins. It also allows OPTIONS preflight before the optional API-key filter.

## Render Environment variables

Set these on the **Offer Letter backend Web Service**:

- `SPRING_DATASOURCE_URL` — full JDBC URL copied from Aiven/MySQL provider.
- `SPRING_DATASOURCE_USERNAME` — exact database username.
- `SPRING_DATASOURCE_PASSWORD` — current database password.
- `JPA_DDL_AUTO` — `update`.
- `APP_ALLOWED_ORIGINS` — exact Vercel frontend origin. Example: `https://offerletter-frontend.vercel.app`. For Vercel previews, add `https://*.vercel.app` only if you intentionally want all Vercel preview origins.
- `APP_PUBLIC_BASE_URL` — `https://offerletter-backend-ihy7.onrender.com`.
- `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` — set new Gmail credentials only after rotating the exposed App Password.

For Aiven, copy the JDBC URL from Aiven's MySQL service Connection Information. It should resemble:

`jdbc:mysql://HOST:PORT/DATABASE?sslMode=REQUIRED`

Do not use localhost, the Aiven console URL, or an arbitrary host. Ensure the Aiven service is Running, public access is enabled if using public networking, and the Aiven IP allowlist/network rules permit the Render service's outbound connection. Use the exact username supplied by Aiven; do not assume it is `root`.

The datasource post-processor also accepts a provider URI of the form `mysql://user:password@host:port/database?...` and normalizes it to JDBC, as the Appraisal backend does. Prefer setting username and password in their separate Render variables rather than embedding credentials in the URL.

## CORS

The frontend origin must match exactly, including `https://` and no path. The screenshot's frontend origin appears to be `https://offerletter-frontend.vercel.app`; set that exact value in `APP_ALLOWED_ORIGINS`. CORS does not make an offline backend reachable.

## Deploy

1. Replace the local backend project with this extracted project.
2. Confirm Render Environment variables above.
3. Commit and push to GitHub.
4. In Render, deploy the latest commit.
5. Check logs for successful Spring Boot startup.
6. Test `https://offerletter-backend-ihy7.onrender.com/api/health`.

If logs still show `Communications link failure`, inspect the JDBC host/port, Aiven service status, public access and IP allowlist. This is a database networking issue, not a CORS setting.

## Local Windows CMD

```cmd
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/offerletter_db
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD
mvn clean spring-boot:run
```

## Notes

- Gmail SMTP on port 587 may be blocked by Render Free. That is separate from MySQL startup.
- Render's filesystem is ephemeral unless you attach persistent storage. Uploaded PDFs in `uploads/` may not survive restarts/redeploys.
- Never commit database passwords, Gmail App Passwords or API keys.
