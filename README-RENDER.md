# Offer Letter backend on Render

- Root Directory: `backend`
- Runtime: Docker
- Dockerfile: `./Dockerfile`
- Health Check Path: `/api/health`
- Do not set a separate Start Command; Dockerfile uses Render's dynamic `PORT`.

Environment variables:
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: hosted MySQL credentials
- `APP_ALLOWED_ORIGINS`: exact Offer Letter Vercel origin
- `APP_PUBLIC_BASE_URL`: actual Offer Letter Render URL
- `UPLOAD_DIR=/var/data/uploads` when a persistent disk is mounted at `/var/data`
- `JPA_DDL_AUTO=update`
- `MAIL_USERNAME`, `MAIL_PASSWORD`: only when email sending is enabled

Render uses its assigned `PORT`; port 9091 is the local-development default only.
The Vercel rewrite must point to this Offer Letter service, not the Appraisal service.
Attach a persistent disk at `/var/data` to retain PDFs after redeploys.
