# Offer Letter Backend — Render deployment

## Project layout
The Render service root must contain `pom.xml`, `Dockerfile`, and `src/`.
In the uploaded repository layout, these files are inside `Sofferletter/offerletter/`.
If that is also the layout in GitHub, set Render **Root Directory** to:
`Sofferletter/offerletter`

## Render Web Service settings
- Runtime: **Docker**
- Dockerfile Path: `./Dockerfile`
- Do not use Node, Yarn, or npm build commands.
- Do not set a separate Start Command; the Dockerfile starts the Spring Boot JAR.
- Ensure the Dockerfile is in the configured Root Directory.

## Required environment variables
Set these in Render → your Web Service → Environment:

- `DB_URL` = the complete JDBC URL supplied by your hosted MySQL provider.
  Example format: `jdbc:mysql://HOST:3306/DATABASE?sslMode=REQUIRED`
  Use the provider's actual URL and SSL options; do not use localhost on Render.
- `DB_USERNAME` = MySQL username
- `DB_PASSWORD` = newly rotated MySQL password
- `APP_ALLOWED_ORIGINS` = `https://YOUR-VERCEL-FRONTEND.vercel.app` (add every production/preview origin that must call this API, comma-separated)
- `APP_PUBLIC_BASE_URL` = `https://YOUR-RENDER-SERVICE.onrender.com`
- `APP_API_KEY` = optional shared API key; if set, the frontend must send it as `x-api-key`
- `MAIL_USERNAME` = Gmail sender address (optional until email sending is used)
- `MAIL_PASSWORD` = newly generated Gmail App Password (optional until email sending is used)
- `JPA_DDL_AUTO` = `update` (suitable for development; use managed migrations for production)

The application supports `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
and `SPRING_DATASOURCE_PASSWORD` as aliases, but prefer the `DB_*` names above.

## Deploy and verify
1. Save the corrected files to GitHub.
2. Confirm Render Root Directory points to the folder containing `pom.xml` and `Dockerfile`.
3. Add the environment variables above. Do not paste secrets into source files or GitHub.
4. Trigger **Manual Deploy → Clear build cache & deploy**.
5. Check logs for `Started OfferLetterApplication`.
6. Open `https://YOUR-RENDER-SERVICE.onrender.com/api/health`.

A successful health response should include `status: UP` and `database: UP`.
The health endpoint queries the database, so it verifies database connectivity too.

## Important storage note
Render's ordinary container filesystem is ephemeral. Uploaded PDFs can disappear after a redeploy/restart.
Attach a Render persistent disk and set `UPLOAD_DIR` to its mount path, or move PDF storage to object storage.

## Security note
The original configuration archive contained hard-coded database and SMTP credentials. They have been removed from the corrected source defaults. Rotate those credentials immediately in the database provider and Gmail, then set the new values only in Render Environment. Do not commit `.env` files or credentials to GitHub.
