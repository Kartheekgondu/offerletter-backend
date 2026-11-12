# Render deployment (backend)
- Runtime: Docker, Dockerfile `./Dockerfile`, Root Directory = folder containing `pom.xml`
- Health Check Path: `/api/health`
- Environment: see `.env.example` (APP_ALLOWED_ORIGINS must be your exact Vercel URL)
- Optional: persistent disk at `/var/data` + `UPLOAD_DIR=/var/data/uploads`
- Test: open `https://offerletter-backend-y0aa.onrender.com/api/health`
