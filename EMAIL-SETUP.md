# Offer Letter email setup

The React frontend creates the current offer-letter PDF and posts it to
`POST /api/send-offer`. The Spring Boot backend sends the PDF as an attachment
using Gmail SMTP. The `Send Mail` button does not open an email application.

## Required backend environment variables

Configure these on the backend hosting provider (Render -> service -> Environment):

- `SMTP_HOST=smtp.gmail.com`
- `SMTP_PORT=587`
- `SMTP_USERNAME=your-company-gmail@gmail.com`
- `SMTP_PASSWORD=your-16-character-google-app-password`
- `APP_ALLOWED_ORIGINS=https://offerletter-frontend.vercel.app`

For Gmail, enable 2-Step Verification and create a Google App Password.
Use the 16-character App Password, not the normal Gmail password. The sender
address is `SMTP_USERNAME`. Never commit credentials to GitHub or put them in
Vercel. The application properties intentionally contain no default credentials.

## Hosting limitation

Render Free web services block outbound SMTP ports 25, 465, and 587. Therefore
this SMTP implementation will not deliver mail from a Render Free instance.
Use a paid Render instance or a hosting provider that permits outbound SMTP.
If the backend must remain on Render Free, replace SMTP with an HTTPS email
provider API (for example, Resend) and configure a verified sender domain.

## Frontend configuration

The frontend defaults to the existing backend URL in `src/lib/api.js`.
If your actual Render backend URL differs, set `VITE_API_BASE_URL` in Vercel
(Project -> Settings -> Environment Variables) to the correct backend base URL,
then redeploy the frontend. If API-key authentication is enabled, set the same
`VITE_API_KEY` value in Vercel as `APP_API_KEY` on the backend.

## Verify

1. Set the four SMTP variables on the backend.
2. Ensure the backend hosting plan permits outbound SMTP.
3. Redeploy/restart the backend.
4. Deploy the updated frontend to Vercel.
5. Generate an offer letter, enter a recipient email address, and click `Send Mail`.
6. Confirm the recipient receives the PDF attachment. The UI reports an error if
   the backend rejects the request; a success message is shown only after SMTP
   accepts the message.
