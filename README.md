# Offer Letter Backend

Spring Boot 3.4.5 backend for the Offer Letter application. This project uses MySQL through a configurable HikariCP data source and Gmail SMTP through Spring `JavaMailSender`.

## Configuration

No passwords or API keys are stored in `application.properties`. Configure these environment variables where the backend runs:

| Variable | Purpose |
|---|---|
| `DB_URL` | JDBC URL, e.g. `jdbc:mysql://HOST:3306/DBNAME?sslMode=REQUIRED` |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JPA_DDL_AUTO` | Hibernate schema mode; defaults to `update` |
| `MAIL_HOST` | SMTP host; defaults to `smtp.gmail.com` |
| `MAIL_PORT` | SMTP port; defaults to `587` |
| `MAIL_USERNAME` | Gmail account address |
| `MAIL_PASSWORD` | Google App Password (not the normal Gmail password) |
| `MAIL_FROM` | Sender address; defaults to `MAIL_USERNAME` |
| `APP_ALLOWED_ORIGINS` | Comma-separated frontend origins |
| `APP_PUBLIC_BASE_URL` | Public backend base URL |
| `UPLOAD_DIR` | Directory for uploaded PDF files |
| `APP_API_KEY` | Optional API key; blank disables API-key validation |
| `PORT` | Hosting platform port; defaults to `9091` |

## Local run on Windows CMD

Open CMD in the folder containing `pom.xml`, then set your local values in that CMD window:

```cmd
set DB_URL=jdbc:mysql://localhost:3306/offerletter_db
set DB_USERNAME=root
set DB_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD
set MAIL_HOST=smtp.gmail.com
set MAIL_PORT=587
set MAIL_USERNAME=YOUR_GMAIL_ADDRESS
set MAIL_PASSWORD=YOUR_GOOGLE_APP_PASSWORD
set MAIL_FROM=YOUR_GMAIL_ADDRESS
set APP_ALLOWED_ORIGINS=http://localhost:5173,https://offerletter-frontend.vercel.app
set APP_PUBLIC_BASE_URL=http://localhost:9091
mvn clean spring-boot:run
```

Use a Google App Password with 2-Step Verification enabled. Do not paste credentials into source files or commit them to Git.

## API endpoints

- `GET /api/health` — backend/database health check
- `POST /api/save-offer` — save offer details and PDF
- `POST /api/send-offer` — email the PDF attachment
- `GET /api/offers` — list saved offers
- `GET /api/offers/{id}` — retrieve an offer
- `GET /api/search-offers?name=...` — search offers

## Render deployment notes

Set the database and mail environment variables in the Render service's Environment settings. Set `APP_ALLOWED_ORIGINS` to the exact deployed frontend origin and `APP_PUBLIC_BASE_URL` to the backend's public URL.

**SMTP limitation:** this implementation sends mail over SMTP port 587. Render Free may restrict outbound SMTP connections, so Gmail SMTP can fail even when credentials are correct. If outbound SMTP is blocked, use an SMTP-capable hosting plan/provider or an HTTPS email API.

**PDF persistence:** the default `uploads/` directory is local container storage. On hosts with ephemeral filesystems, uploaded PDFs may disappear after redeploy/restart. Use a persistent disk or object storage if PDFs must remain available.

## Build

```cmd
mvn clean package
```

The ZIP excludes Git history, IDE metadata, build output, uploaded files, and credentials. It contains the backend only; it does not contain the React/Vite frontend.
