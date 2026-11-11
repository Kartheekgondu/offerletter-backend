
package com.uxinterfacely.offerletter.service;

import com.uxinterfacely.offerletter.model.OfferSend;
import com.uxinterfacely.offerletter.repository.OfferSendRepository;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
public class OfferLetterService {

    private final OfferSendRepository repository;
    private final FileStorageService storage;
    private final JavaMailSender mailSender;

    @Value("${app.public-base-url:http://localhost:9091}")
    private String publicBaseUrl;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${spring.mail.username:}")
    private String fromAddress;


    public OfferLetterService(
            OfferSendRepository repository,
            FileStorageService storage,
            JavaMailSender mailSender) {

        this.repository = repository;
        this.storage = storage;
        this.mailSender = mailSender;
    }


    // ==========================================
    // SAVE OFFER LETTER
    // ==========================================

    @Transactional
    public Map<String, Object> save(
            Map<String, String> f,
            MultipartFile pdf) throws Exception {

        String candidateName = required(f, "candidateName");

        if (candidateName.length() > 180) {
            throw new IllegalArgumentException(
                    "Candidate name must be 180 characters or fewer"
            );
        }

        if (pdf == null || pdf.isEmpty()) {
            throw new IllegalArgumentException(
                    "Generated PDF is missing. Generate the letter again and retry."
            );
        }

        String pdfPath = storage.store(pdf);

        OfferSend entity = toEntity(f);

        entity.setPdfUrl(
                publicBaseUrl.replaceAll("/$", "") + pdfPath
        );

        entity.setStatus("SAVED");

        entity = repository.save(entity);

        return Map.of(
                "success", true,
                "message", "Offer letter saved",
                "id", entity.getId(),
                "pdfUrl", entity.getPdfUrl()
        );
    }


    // ==========================================
    // SEND OFFER LETTER EMAIL
    // ==========================================

    public Map<String, Object> send(
            Map<String, String> f,
            MultipartFile pdf) throws Exception {

        String recipient = required(f, "recipient");

        String candidate = value(f, "candidateName", "Candidate");
        String role = value(f, "jobTitle", "the offered role");
        String location = value(f, "workLocation", "");
        String doj = value(f, "dojDisplay", "");

        if (pdf == null || pdf.isEmpty()) {
            throw new IllegalArgumentException(
                    "Offer letter PDF attachment is missing"
            );
        }

        String stored = storage.store(pdf);

        Path pdfFile = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize()
                .resolve(Paths.get(stored).getFileName())
                .normalize();

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);

            if (fromAddress != null && !fromAddress.isBlank()) {
                helper.setFrom(fromAddress);
            }

            helper.setSubject("Offer Letter - " + candidate);

            // CORRECTED EMAIL MESSAGE
            helper.setText(
                    "Dear " + candidate + ",\n\n"
                    + "Please find attached your offer letter for "
                    + role
                    + (location.isBlank()
                            ? ""
                            : " at " + location)
                    + (doj.isBlank()
                            ? ""
                            : ". Date of joining: " + doj)
                    + ".\n\n"
                    + "Regards,\n"
                    + "UXINTERFACELY IT SOLUTIONS LLP",
                    false
            );

            helper.addAttachment(
                    candidate.replaceAll("[^A-Za-z0-9_-]", "_")
                            + "_Offer_Letter.pdf",
                    new FileSystemResource(pdfFile)
            );

            mailSender.send(message);

            return Map.of(
                    "success", true,
                    "message", "Offer letter email sent successfully"
            );

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Email could not be sent. Verify the mail configuration and try again.",
                    ex
            );
        }
    }


    // ==========================================
    // BACKEND HEALTH CHECK
    // ==========================================

    public Map<String, Object> health() {

        long total = repository.count();

        return Map.of(
                "status", "UP",
                "service", "offer-letter-api",
                "database", "UP",
                "savedOffers", total
        );
    }


    // ==========================================
    // GET ALL PREVIOUS OFFER LETTERS
    // ==========================================

    public Map<String, Object> listRecent() {

        List<Map<String, Object>> results =
                repository.findAllByOrderByCreatedAtDesc(PageRequest.of(
                                0, 50, Sort.by(Sort.Direction.DESC, "createdAt")
                        ))
                        .stream()
                        .map(this::summary)
                        .toList();

        return Map.of(
                "success", true,
                "results", results
        );
    }


    // ==========================================
    // GET SELECTED OFFER LETTER
    // ==========================================

    public Map<String, Object> getOffer(Long id) {

        OfferSend o = repository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Offer letter not found"
                        )
                );

        Map<String, Object> offer = new LinkedHashMap<>();

        offer.put("id", o.getId());
        offer.put("candidateName", o.getCandidateName());
        offer.put("jobTitle", o.getJobTitle());
        offer.put("recipientEmail", o.getRecipientEmail());

        offer.put(
                "dateOfJoining",
                o.getDateOfJoining() == null
                        ? null
                        : o.getDateOfJoining().toString()
        );

        offer.put(
                "letterDate",
                o.getLetterDate() == null
                        ? null
                        : o.getLetterDate().toString()
        );

        offer.put("department", o.getDepartment());
        offer.put("workLocation", o.getWorkLocation());
        offer.put("roleDescription", o.getRoleDescription());

        offer.put("annualCtc", o.getAnnualCtc());
        offer.put("noticePeriodDays", o.getNoticePeriodDays());

        offer.put("includeBond", o.getIncludeBond());
        offer.put("bondYears", o.getBondYears());
        offer.put("bondAmount", o.getBondAmount());

        offer.put("pfIncluded", o.getPfIncluded());
        offer.put("employeePf", o.getEmployeePf());
        offer.put("employerPf", o.getEmployerPf());

        offer.put("variablePayIncluded", o.getVariablePayIncluded());
        offer.put("variablePayAmount", o.getVariablePayAmount());

        offer.put("tdsIncluded", o.getTdsIncluded());
        offer.put("tdsAmount", o.getTdsAmount());

        offer.put("pdfUrl", o.getPdfUrl());
        offer.put("status", o.getStatus());
        offer.put("createdAt", o.getCreatedAt());

        return Map.of(
                "success", true,
                "offer", offer
        );
    }


    // ==========================================
    // PREVIOUS OFFER LETTER SUMMARY
    // ==========================================

    private Map<String, Object> summary(OfferSend o) {
        // Stable camelCase response contract shared by the React application.
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", o.getId());
        row.put("candidateName", o.getCandidateName());
        row.put("jobTitle", o.getJobTitle());
        row.put("recipientEmail", o.getRecipientEmail());
        row.put("department", o.getDepartment());
        row.put("workLocation", o.getWorkLocation());
        row.put("status", o.getStatus());
        row.put("dateOfJoining", o.getDateOfJoining() == null ? null : o.getDateOfJoining().toString());
        row.put("letterDate", o.getLetterDate() == null ? null : o.getLetterDate().toString());
        row.put("annualCtc", o.getAnnualCtc());
        row.put("pdfUrl", o.getPdfUrl());
        row.put("createdAt", o.getCreatedAt());
        return row;
    }


    // ==========================================
    // SEARCH PREVIOUS OFFER LETTERS
    // ==========================================

    public Map<String, Object> search(String name) {

        String query = name == null ? "" : name.trim();

        if (query.isBlank()) {
            return Map.of(
                    "success", true,
                    "results", List.of()
            );
        }

        Long id = null;

        try {
            id = Long.valueOf(query);
        } catch (NumberFormatException ignored) {
            // Continue with text search
        }

        List<Map<String, Object>> results =
                repository.searchOffers(
                        query,
                        id,
                        PageRequest.of(
                                0,
                                50,
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "createdAt"
                                )
                        )
                )
                .getContent()
                .stream()
                .map(this::summary)
                .toList();

        return Map.of(
                "success", true,
                "results", results
        );
    }


    // ==========================================
    // CONVERT FORM DATA TO DATABASE ENTITY
    // ==========================================

    private OfferSend toEntity(Map<String, String> f) {

        OfferSend o = new OfferSend();

        o.setCandidateName(
                value(f, "candidateName", "Candidate")
        );

        o.setJobTitle(value(f, "jobTitle", ""));
        o.setRecipientEmail(value(f, "recipientEmail", ""));

        o.setDateOfJoining(date(f, "doj"));
        o.setLetterDate(date(f, "letterDate"));

        o.setDepartment(value(f, "department", ""));
        o.setWorkLocation(value(f, "workLocation", ""));
        o.setRoleDescription(value(f, "roleDescription", ""));

        o.setAnnualCtc(number(f, "annualCtc"));
        o.setNoticePeriodDays(integer(f, "noticePeriodDays"));

        o.setIncludeBond(bool(f, "includeBond"));
        o.setBondYears(integer(f, "bondYears"));
        o.setBondAmount(number(f, "bondAmount"));

        o.setPfIncluded(bool(f, "pfIncluded"));
        o.setEmployeePf(number(f, "employeePf"));
        o.setEmployerPf(number(f, "employerPf"));

        o.setVariablePayIncluded(bool(f, "variablePayIncluded"));
        o.setVariablePayAmount(number(f, "variablePayAmount"));

        o.setTdsIncluded(bool(f, "tdsIncluded"));
        o.setTdsAmount(number(f, "tdsAmount"));

        return o;
    }


    // ==========================================
    // HELPER METHODS
    // ==========================================

    private String value(
            Map<String, String> f,
            String k,
            String d) {

        String v = f.get(k);

        return v == null ? d : v.trim();
    }


    private String required(
            Map<String, String> f,
            String k) {

        String v = value(f, k, "");

        if (v.isBlank()) {
            throw new IllegalArgumentException(
                    k + " is required"
            );
        }

        return v;
    }


    private Long number(
            Map<String, String> f,
            String k) {

        try {

            String v = value(f, k, "");

            return v.isBlank()
                    ? null
                    : Long.valueOf(v);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    k + " must be a whole number"
            );
        }
    }


    private Integer integer(
            Map<String, String> f,
            String k) {

        try {

            String v = value(f, k, "");

            return v.isBlank()
                    ? null
                    : Integer.valueOf(v);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    k + " must be an integer"
            );
        }
    }


    private Boolean bool(
            Map<String, String> f,
            String k) {

        String v = value(f, k, "");

        return v.isBlank()
                ? null
                : Boolean.valueOf(v);
    }


    private LocalDate date(
            Map<String, String> f,
            String k) {

        String v = value(f, k, "");

        try {

            return v.isBlank()
                    ? null
                    : LocalDate.parse(v);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    k + " must use YYYY-MM-DD"
            );
        }
    }


    private String safeMessage(Exception ex) {

        String m = ex.getMessage();

        return m == null
                ? "unexpected error"
                : m.substring(0, Math.min(m.length(), 240));
    }

}
