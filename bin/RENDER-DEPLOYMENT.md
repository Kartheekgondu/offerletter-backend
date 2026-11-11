# Offer Letter backend — Render deployment

## Render service settings
- Runtime: Docker
- Root Directory: `backend` (or the directory containing this backend's `pom.xml` and `Dockerfile`)
- Dockerfile Path: `./Dockerfile`
- Leave Start Command empty; the Dockerfile starts Spring Boot.

## Required Render environment
- `DB_URL`: hosted MySQL JDBC URL (`jdbc:mysql://HOST:3306/DB?sslMode=REQUIRED`)
- `DB_USERNAME`: database username
- `DB_PASSWORD`: newly rotated database password
- `APP_ALLOWED_ORIGINS`: exact Vercel origin, e.g. `https://YOUR-PROJECT.vercel.app`. For multiple domains, comma-separate the exact origins.
- `APP_PUBLIC_BASE_URL`: `https://YOUR-RENDER-SERVICE.onrender.com`
- `UPLOAD_DIR`: `/var/data/uploads` if you attach a Render persistent disk at `/var/data`; otherwise use `uploads` (files may be lost on restart/redeploy).
- `JPA_DDL_AUTO`: `update` for this existing schema setup.
- `MAIL_USERNAME` and `MAIL_PASSWORD`: optional; configure a Gmail App Password if email sending is required.
- `APP_API_KEY`: optional; leave unset unless you intentionally use API-key protection.

## Deploy and test
1. In Render, confirm the root directory contains `pom.xml` and `Dockerfile`.
2. Add the environment variables above using your real provider values. Never commit credentials.
3. Deploy and wait for `Started OfferLetterApplication`.
4. Open `https://YOUR-RENDER-SERVICE.onrender.com/api/health`. It should report the service and database as UP.
5. In Vercel, set `VITE_API_BASE` to the Render URL with no `/api` suffix, then redeploy the frontend.
6. Test save, search, and email from the deployed Vercel website.

## Important
The backend accepts PDFs up to 15 MB. Render's local filesystem is ephemeral unless a persistent disk is mounted. This ZIP does not contain your database credentials; supply them through Render environment variables.
