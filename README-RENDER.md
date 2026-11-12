# Offer Letter backend deployment (Render)

## Email configuration

This backend sends offer-letter emails through the Resend HTTPS API, not Gmail SMTP.
Render Free web services block outbound SMTP ports 25, 465, and 587.

1. Create an account at https://resend.com/.
2. Verify a domain you own in Resend.
3. Create an API key.
4. In Render -> backend service -> Environment, set:
   - `RESEND_API_KEY`: your Resend API key
   - `RESEND_FROM_EMAIL`: a sender address on your verified domain, e.g. `Offer Letters <offers@yourdomain.com>`
5. Keep the database and other required environment variables configured.
6. Deploy the backend from the `main` branch.

## Other settings

- Frontend origin: `https://offerletter-frontend.vercel.app`
- Backend URL: `https://offerletter-backend-316h.onrender.com`
- Health check: `/api/health`
- Optional persistent disk mount: `/var/data`; set `UPLOAD_DIR=/var/data/uploads`.

Never commit API keys, database passwords, or other secrets. Rotate credentials previously committed or shared.
