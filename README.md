# Offer Letter Backend — Gmail SMTP (Appraisal-style)

This backend uses Spring Boot `JavaMailSender` and Gmail SMTP.

## Configuration

Set these environment variables in the environment where Spring Boot runs:

- `MAIL_HOST` — defaults to `smtp.gmail.com`
- `MAIL_PORT` — defaults to `587`
- `MAIL_USERNAME` — Gmail account address
- `MAIL_PASSWORD` — Google App Password (not the normal Gmail password)
- `MAIL_FROM` — sender address; defaults to `MAIL_USERNAME`
- `DB_URL` — JDBC URL, e.g. `jdbc:mysql://localhost:3306/offerletter_db`
- `DB_USERNAME` — database username
- `DB_PASSWORD` — database password
- `APP_ALLOWED_ORIGINS` — comma-separated frontend origins
- `APP_PUBLIC_BASE_URL` — public backend URL
- `UPLOAD_DIR` — PDF storage directory

No credentials are stored in `application.properties`.

## Windows CMD local run

Run these commands in the same CMD window used to start the backend:

```cmd
set MAIL_HOST=smtp.gmail.com
set MAIL_PORT=587
set MAIL_USERNAME=your-gmail-address
set MAIL_PASSWORD=your-16-character-google-app-password
set MAIL_FROM=your-gmail-address
set DB_URL=jdbc:mysql://localhost:3306/offerletter_db
set DB_USERNAME=root
set DB_PASSWORD=your-local-mysql-password
mvn clean spring-boot:run
```

Use a Google App Password with 2-Step Verification enabled. Never commit it to Git.

## Email behavior

`OfferLetterService.send(...)` validates the recipient and PDF, creates a MIME message, attaches the generated PDF, and sends it using `JavaMailSender`. The API returns success only after `mailSender.send(...)` completes.

## Hosting note

This implementation uses SMTP port 587. Confirm that your hosting provider permits outbound SMTP. Render Free may block SMTP egress; if so, this exact Gmail SMTP implementation will not work from that plan. Use an SMTP-capable host/plan or a supported HTTPS email provider.

## Build

```cmd
mvn clean package
```

The ZIP intentionally excludes `.git`, IDE metadata, build output, and all credentials. Apply the corrected source files to your existing repository; do not replace your `.git` directory.
