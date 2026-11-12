# Offer Letter email setup (SMTP)

Offer Letter now uses the same Spring `JavaMailSender` + `MimeMessageHelper` approach as the Appraisal backend. The frontend continues to call `POST /api/send-offer` as multipart form data with `recipient` and `pdf` fields.

## Render environment variables

Configure these on the Offer Letter backend service:

- `SPRING_MAIL_HOST=smtp.gmail.com`
- `SPRING_MAIL_PORT=587`
- `SPRING_MAIL_USERNAME=<your Gmail address>`
- `SPRING_MAIL_PASSWORD=<a newly generated Google App Password>`

Do not commit real credentials to Git or put SMTP credentials in Vercel frontend environment variables.

## Important deployment/network note

The screenshot's `SocketTimeoutException` means the backend could not establish a TCP connection to `smtp.gmail.com:587`. This is a network-level timeout, not a frontend error or an incorrect recipient address. This code change makes Offer Letter's mail implementation match Appraisal, but it cannot by itself open blocked outbound SMTP traffic. After deployment, test the backend host's outbound access to `smtp.gmail.com:587`. If Render blocks outbound SMTP on the selected service/plan, use an HTTPS email provider/API or a host that permits SMTP egress.

## Security

The supplied project archives contain a Gmail app password in a properties file. Treat that credential as exposed: revoke it in Google Account security and create a new App Password. Supply the replacement only through Render environment variables.
