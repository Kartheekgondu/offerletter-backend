# Offer Letter project — SMTP email implementation

This version adapts the Appraisal project's `JavaMailSender` + `MimeMessageHelper` pattern to Offer Letter.
It does **not** use Resend and does **not** require `RESEND_API_KEY`.

## What changed
- Backend `pom.xml`: added `spring-boot-starter-mail`.
- `OfferLetterService`: sends a personalized email using `JavaMailSender`, attaches the generated offer PDF, validates recipient and PDF signature, and returns success only after `mailSender.send` returns.
- `application.properties`: SMTP settings are read from environment variables. No API key, Gmail password, or database password is hardcoded.
- Frontend API and Vercel routing are retained from the uploaded project; the send endpoint remains `POST /api/send-offer`.

## Local Windows CMD setup
Open CMD in the backend folder and set values in the same window before starting Spring Boot:

```cmd
set MAIL_HOST=smtp.gmail.com
set MAIL_PORT=587
set MAIL_USERNAME=your-sender@gmail.com
set MAIL_PASSWORD=your-16-character-google-app-password
set MAIL_FROM=your-sender@gmail.com
set DB_URL=jdbc:mysql://localhost:3306/offerletter_db
set DB_USERNAME=root
set DB_PASSWORD=your-local-mysql-password
mvn clean spring-boot:run
```

Use a Google App Password with 2-Step Verification enabled. Do not use your normal Google account password. Keep credentials out of source control.

In a second CMD, open the frontend folder and run:

```cmd
npm install
npm run dev
```

## Render deployment warning
Render Free does not permit outbound SMTP on ports 25, 465, or 587. This SMTP version therefore will **not send email from Render Free** even if credentials are correct. Use a hosting plan/provider that permits outbound SMTP, or switch the backend email transport to an HTTPS email API (for example Resend) for Render Free. Do not bypass provider restrictions.

For a SMTP-capable host, configure these environment variables on the backend service:
- `MAIL_HOST` (usually `smtp.gmail.com`)
- `MAIL_PORT` (usually `587`)
- `MAIL_USERNAME`
- `MAIL_PASSWORD` (Google App Password)
- `MAIL_FROM` (same authorized sender address)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `APP_ALLOWED_ORIGINS` (your exact Vercel origin)
- `APP_PUBLIC_BASE_URL` (your backend public URL)

## Deploy/build
Backend:
```cmd
mvn clean package
```
Frontend:
```cmd
npm install
npm run build
```

## Test
- Local backend health: `http://localhost:9091/api/health`
- Frontend local: `http://localhost:5173`
- Test sending to an email address you control. Confirm receipt and PDF attachment.

The source files have been updated, but a real email delivery test requires your SMTP credentials and a network that permits SMTP. Do not share credentials in chat or commit them to GitHub.
