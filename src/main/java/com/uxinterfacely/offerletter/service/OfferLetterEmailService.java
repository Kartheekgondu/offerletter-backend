package com.uxinterfacely.offerletter.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OfferLetterEmailService {

    private final JavaMailSender mailSender;

    public OfferLetterEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOfferLetter(String recipient,
                                String candidateName,
                                String jobTitle,
                                String workLocation,
                                String dateOfJoining,
                                MultipartFile pdf) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient email address is missing.");
        }
        if (pdf == null || pdf.isEmpty()) {
            throw new IllegalArgumentException("Offer letter PDF attachment is missing.");
        }

        String name = candidateName == null || candidateName.isBlank()
                ? "Candidate" : candidateName.trim();
        String role = jobTitle == null || jobTitle.isBlank()
                ? "the offered role" : jobTitle.trim();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient.trim());
            helper.setSubject("Offer Letter - " + name);

            StringBuilder body = new StringBuilder()
                    .append("Dear ").append(name).append(",\n\n")
                    .append("Please find attached your offer letter for ")
                    .append(role);

            if (workLocation != null && !workLocation.isBlank()) {
                body.append(" at ").append(workLocation.trim());
            }
            if (dateOfJoining != null && !dateOfJoining.isBlank()) {
                body.append(". Date of joining: ").append(dateOfJoining.trim());
            }

            body.append(".\n\nRegards,\nHR Department\nUXINTERFACELY IT SOLUTIONS LLP");

            helper.setText(body.toString());
            String safeName = name.replaceAll("[^A-Za-z0-9_-]", "_");
            helper.addAttachment(
                    safeName + "_Offer_Letter.pdf",
                    new ByteArrayResource(pdf.getBytes()),
                    "application/pdf"
            );

            mailSender.send(message);
        } catch (MessagingException | MailException exception) {
            throw new IllegalStateException(
                    "Unable to send offer letter email. Check SMTP settings and outbound mail connectivity.",
                    exception
            );
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(
                    "Unable to read the generated offer letter PDF attachment.",
                    exception
            );
        }
    }
}
