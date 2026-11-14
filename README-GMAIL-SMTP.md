# Offer Letter Backend - Gmail SMTP (Appraisal-style)

This version removes the Resend API integration and uses Spring Boot `JavaMailSender` with Gmail SMTP, following the email pattern in the supplied Appraisal backend. The offer-letter PDF is attached as a MIME attachment.

## Required environment variables

Set these outside source control:

- `MAIL_HOST` = `smtp.gmail.com`
- `MAIL_PORT` = `587`
- `MAIL_USERNAME` = your Gmail address
- `MAIL_PASSWORD` = a Google App Password (not your normal Gmail password)
- `MAIL_FROM` = the same Gmail address, or an authorized sender
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` = database connection details

Do not put credentials in `application.properties`, Java source, or Git. Revoke any credentials previously exposed in commits or chat and generate replacements.

## Run locally (Windows CMD)

From this backend folder:

```cmd
set MAIL_HOST=smtp.gmail.com
set MAIL_PORT=587
set MAIL_USERNAME=your-address@gmail.com
set MAIL_PASSWORD=YOUR_GOOGLE_APP_PASSWORD
set MAIL_FROM=your-address@gmail.com
set DB_URL=jdbc:mysql://localhost:3306/offerletter_db
set DB_USERNAME=root
set DB_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD
mvn clean spring-boot:run
```

Run the `set` commands in the same CMD window used to start Maven. The app listens on port 9091 by default.

## Important hosting limitation

Gmail SMTP requires outbound port 587. Render Free generally blocks outbound SMTP ports. This SMTP version is suitable for local testing or a host that permits SMTP egress; it will not work on a Render Free service if port 587 is blocked. Use an SMTP-capable paid host or an HTTPS email provider for Render Free.

## GitHub secret-scanning note

Removing Resend from the current source does not erase the Resend key from previous Git commits. Revoke the exposed key, then rewrite/remove the secret-containing commits before pushing. Do not use GitHub's secret bypass link.
