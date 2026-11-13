# Offer Letter email setup (Resend HTTPS API)

The frontend posts the generated offer-letter PDF to `POST /api/send-offer`.
The backend sends it as an attachment through Resend's HTTPS API. This avoids
SMTP ports, which are blocked on Render Free.

## 1. Configure Resend

1. Create an account at https://resend.com/.
2. Add and verify a domain you control in Resend. Publish the DNS records Resend provides.
3. Create a Resend API key.
4. Set a sender address on that verified domain, e.g. `Offer Letters <offers@yourdomain.com>`.

A verified sender domain is required to send to arbitrary recipients. Do not use
a sender address you do not control.

## 2. Configure Render backend environment

In Render -> backend service -> Environment, add:

- `RESEND_API_KEY` = your Resend API key
- `RESEND_FROM_EMAIL` = `Offer Letters <offers@yourdomain.com>`
- `APP_ALLOWED_ORIGINS` = `https://offerletter-frontend.vercel.app`
- `APP_PUBLIC_BASE_URL` = your actual Render backend URL
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` = your database connection values

Never put the Resend API key in Vercel or frontend code. Never commit secrets to GitHub.

## 3. Configure Vercel frontend

Set `VITE_API_BASE_URL` to the exact public URL of your Render backend, without
a trailing slash. If API-key authentication is enabled, set `VITE_API_KEY` to
the same value as backend `APP_API_KEY`. Redeploy the frontend after changes.

## 4. Deploy and test

1. Push the updated backend to GitHub and deploy/redeploy the Render service.
2. Confirm `GET /api/health` responds successfully.
3. Deploy the frontend to Vercel.
4. Generate an offer letter and send it to an address you are authorized to contact.
5. Confirm the PDF arrives. The API reports success only after Resend accepts
   the request. Provider acceptance does not guarantee inbox delivery; check
   Resend delivery events and spam folders if necessary.
