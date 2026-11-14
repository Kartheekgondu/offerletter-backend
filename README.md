# Offer Letter Backend — Render / MySQL configuration

This is the Spring Boot backend. It uses MySQL via a custom HikariCP `DataSourceConfig` and Gmail SMTP via `JavaMailSender`.

## Fixing Render's `Communications link failure`

The log shows the application cannot establish a TCP/JDBC connection to MySQL. This happens before Hibernate can initialize. It is usually caused by an incorrect database host/port, wrong JDBC URL, database not publicly reachable, IP allowlist, or a stopped/expired database. It is not a Gmail SMTP problem.

### Render Environment variables

In Render → your **backend Web Service** → Environment, set:

- `DB_URL`: exact JDBC URL from your database provider. For Aiven MySQL, use the provider's JDBC format:
  `jdbc:mysql://YOUR-HOST:YOUR-PORT/YOUR-DATABASE?sslMode=REQUIRED`
- `DB_USERNAME`: exact database username shown by Aiven (often `avnadmin`, not `root`)
- `DB_PASSWORD`: database password
- `JPA_DDL_AUTO`: `update`
- `APP_ALLOWED_ORIGINS`: `http://localhost:5173,https://YOUR-FRONTEND.vercel.app`
- `APP_PUBLIC_BASE_URL`: `https://YOUR-BACKEND.onrender.com`
- `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`: set only after rotating the exposed Google App Password.

Do not add quotes around environment variable values. Do not use `localhost` or `127.0.0.1` for a hosted Aiven database. Do not use the Aiven console/web URL. Copy the host, port, database name, username, and JDBC URL from Aiven → your MySQL service → Connection Information.

### Aiven network access

In Aiven, verify the MySQL service is running and that its allowed IP/network access includes the Render outbound source. If your Aiven plan supports public access, ensure it is enabled. If Aiven requires an IP allowlist and Render's outbound IPs are not allowed, the connection will fail. Check Render's outbound IP information for the service/region and configure the Aiven allowlist accordingly, or use a database networking option supported by both providers.

### After updating variables

1. Save changes in Render.
2. Trigger **Manual Deploy → Deploy latest commit** (or wait for auto-deploy).
3. Inspect the new logs. The datasource should log only a masked host/URL, never the password.
4. Test `https://YOUR-BACKEND.onrender.com/api/health`.

The `/api/health` endpoint queries the database, so it only returns healthy when the database is reachable.

## Local run (Windows CMD)

From the folder containing `pom.xml`:

```cmd
set DB_URL=jdbc:mysql://localhost:3306/offerletter_db
set DB_USERNAME=root
set DB_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD
set MAIL_USERNAME=YOUR_GMAIL_ADDRESS
set MAIL_PASSWORD=YOUR_NEW_GOOGLE_APP_PASSWORD
set MAIL_FROM=YOUR_GMAIL_ADDRESS
mvn clean spring-boot:run
```

## Important SMTP note

This project retains Gmail SMTP on port 587. Render Free may restrict outbound SMTP. If email sending times out on Render Free, that is a hosting-network restriction; use a host/provider that permits SMTP or an HTTPS email API.

## PDF storage note

`uploads/` is local container storage. Render filesystems are ephemeral unless a persistent disk is attached. Use persistent storage or object storage if saved PDFs must survive restarts/redeploys.

## Build

```cmd
mvn clean package
```

No credentials or Git history are included in this ZIP.
