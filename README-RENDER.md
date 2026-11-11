# Offer Letter backend on Render

- Root Directory: `backend`
- Runtime: Docker
- Dockerfile: `./Dockerfile`
- Health Check Path: `/api/health`
- Do not set a separate Start Command; the Dockerfile uses Render's `PORT`.

Set these environment variables in Render:
- `DB_URL` = your hosted MySQL JDBC URL, not localhost
- `DB_USERNAME` = database username
- `DB_PASSWORD` = database password
- `APP_ALLOWED_ORIGINS` = exact Vercel origin, e.g. `https://your-project.vercel.app`
- `APP_PUBLIC_BASE_URL` = actual Render public URL, e.g. `https://your-service.onrender.com`
- `UPLOAD_DIR` = `/var/data/uploads` when a persistent disk is mounted at `/var/data`
- `JPA_DDL_AUTO` = `update`
- `MAIL_USERNAME` and `MAIL_PASSWORD` if email sending is required

The service listens on `${PORT}` in Render and defaults to port 9091 only for local development.
Do not set Render's port to 9091 manually.

Attach a Render persistent disk at `/var/data` to keep saved PDFs through deploys/restarts.
Without persistent storage, the database records may remain but uploaded PDFs can be lost.
