# Render deployment (backend)

## Render service settings
- Runtime: Docker.
- Root Directory: `offerletter` (the folder containing `pom.xml` and `Dockerfile` in this repository).
- Dockerfile Path: `Dockerfile` relative to the Root Directory.
- Health Check Path: `/api/health`.
- Set the environment variables below in Render -> your service -> Environment.

## Required environment variables
Copy the variable names from `.env.example` and supply your own values:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_ALLOWED_ORIGINS=https://offerletter-frontend.vercel.app`
- `APP_PUBLIC_BASE_URL=https://offerletter-backend-316h.onrender.com`

For PDF persistence, attach a Render persistent disk mounted at `/var/data` and set
`UPLOAD_DIR=/var/data/uploads`. Without a persistent disk, files written to the container
filesystem may be lost on redeploy/restart.

Optional:
- `MAIL_USERNAME` and `MAIL_PASSWORD` for SMTP email.
- `APP_API_KEY` to enable API-key authentication. If configured, set the exact same value
  as `VITE_API_KEY` in Vercel. Leave both empty to disable API-key authentication.

## Deploy and verify
1. Push the backend changes to the Git repository connected to Render.
2. Confirm the Root Directory and Dockerfile settings above.
3. Add the environment variables and deploy.
4. Open `https://offerletter-backend-316h.onrender.com/api/health`.
5. The health endpoint should return a successful JSON response.

## Frontend connection
The Vercel frontend at `https://offerletter-frontend.vercel.app/` calls
`https://offerletter-backend-316h.onrender.com` directly in production. The frontend's
`vercel.json` also contains a `/backend/*` rewrite to this same Render service for
proxy-based requests.

Ensure the Render CORS allowlist contains the exact Vercel origin:
`https://offerletter-frontend.vercel.app` (no trailing slash).
