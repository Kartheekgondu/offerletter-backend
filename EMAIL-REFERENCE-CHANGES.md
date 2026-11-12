# Email changes based on the Appraisal Letter project

The Appraisal project uses Spring JavaMailSender with Gmail SMTP (smtp.gmail.com:587) and a MIME PDF attachment. The Offer Letter project already used the same SMTP pattern, so copying the Appraisal SMTP code would not fix the Render connection timeout.

This corrected version preserves the Appraisal-style personalized email and PDF attachment behavior, but sends through the Resend HTTPS API instead of SMTP. This avoids Render Free's outbound SMTP port restriction.

Configure RESEND_API_KEY and RESEND_FROM_EMAIL in Render. The sender address must belong to a domain verified in Resend.
