# Email changes based on the Appraisal Letter project

The Offer Letter backend now follows the Appraisal backend's Spring `JavaMailSender` approach:

- SMTP delivery through Gmail
- MIME email with candidate name, job title, date of joining and work location
- Generated PDF attached directly from memory
- Recipient comes from the frontend input, so any valid recipient address can be used
- No `RESEND_API_KEY` or `RESEND_FROM_EMAIL`

The frontend no longer uses `mailto:` and does not trigger a PDF download when emailing. It posts the generated PDF to `/api/send-offer`; the backend sends it and only then returns success.

SMTP credentials are supplied through `SMTP_USERNAME` and `SMTP_PASSWORD` in Render. They are deliberately not hardcoded.

Important: Render Free web services block outbound SMTP ports 25, 465 and 587. This approach requires a paid Render instance or a hosting environment that permits outbound SMTP. See README-RENDER.md.
