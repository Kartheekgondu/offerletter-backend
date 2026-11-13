# Offer Letter backend deployment (Render)

## Email configuration — Gmail SMTP

The backend uses Spring Boot `JavaMailSender` and attaches the generated offer-letter PDF. No Resend account or Resend environment variables are used.

Set these variables in Render -> backend service -> Environment:

- `SMTP_HOST=smtp.gmail.com`
- `SMTP_PORT=587`
- `SMTP_USERNAME=your-company-gmail@gmail.com`
- `SMTP_PASSWORD=your-16-character-google-app-password`

Create a Google App Password after enabling 2-Step Verification. Do not use your normal Gmail password. Keep credentials only in Render, never in GitHub or Vercel.

**Render plan limitation:** Render's free web services block outbound SMTP on ports 25, 465, and 587. This Gmail SMTP implementation will therefore time out on a Render Free web service. To use it on Render, upgrade the backend to a paid instance. Alternatively, host the backend on a platform that permits outbound SMTP, or use an HTTPS email API (which you previously asked not to use).

## Other settings

- Frontend origin: `https://offerletter-frontend.vercel.app`
- Backend URL: `https://offerletter-backend-o6jw.onrender.com`
- Health check: `/api/health`
- Optional persistent disk mount: `/var/data`; set `UPLOAD_DIR=/var/data/uploads`.

Never commit API keys, SMTP passwords, database passwords, or other secrets. Rotate any credentials that were exposed in uploaded archives or Git history.
