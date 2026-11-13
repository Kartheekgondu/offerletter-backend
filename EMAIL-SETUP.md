# Offer Letter email setup (Gmail SMTP)

The frontend sends the generated PDF to `POST /api/send-offer`. The backend uses Spring Boot `JavaMailSender`, following the Appraisal project pattern. The PDF is attached directly to the email; the frontend does not download it or open `mailto:`.

## Render backend environment variables

Set these on the backend service in Render -> Environment:

- `SMTP_HOST=smtp.gmail.com`
- `SMTP_PORT=587`
- `SMTP_USERNAME=your-company-gmail@gmail.com`
- `SMTP_PASSWORD=your-16-character-google-app-password`

There is no `RESEND_API_KEY` or `RESEND_FROM_EMAIL`.

For Gmail, enable 2-Step Verification and create a Google App Password. Use the 16-character App Password as `SMTP_PASSWORD`, not your normal Gmail password. The sender address is taken from `SMTP_USERNAME`. Keep credentials only in Render; never commit them to GitHub or put them in Vercel.

## Important Render Free limitation

Render announced that free web services block outbound SMTP ports 25, 465, and 587. Therefore this Gmail SMTP implementation will time out on a Render Free web service. To use this exact SMTP implementation on Render, upgrade the backend to a paid instance. Alternatively, host the backend somewhere that permits outbound SMTP. If you must remain on Render Free, an HTTPS mail API or Gmail API OAuth integration is required instead of SMTP.

## Test

1. Configure SMTP variables on Render.
2. Confirm the backend is running and redeploy it.
3. Deploy the updated frontend to Vercel.
4. Generate an offer letter, enter any valid recipient address, and click Email Offer Letter.
5. Confirm the recipient receives the email with the offer-letter PDF attached.
