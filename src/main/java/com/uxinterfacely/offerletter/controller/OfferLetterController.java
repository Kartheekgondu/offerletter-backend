package com.uxinterfacely.offerletter.controller;

import com.uxinterfacely.offerletter.service.OfferLetterService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api")
public class OfferLetterController {
    private final OfferLetterService service;

    public OfferLetterController(OfferLetterService service) {
        this.service = service;
    }

    @PostMapping(value = "/save-offer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> save(
            @RequestParam Map<String, String> fields,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf) throws Exception {
        return ResponseEntity.ok(service.save(fields, pdf));
    }

    @PostMapping(value = "/send-offer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> send(
            @RequestParam Map<String, String> fields,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf) throws Exception {
        return ResponseEntity.ok(service.send(fields, pdf));
    }

    // Main history route plus compatibility aliases for older frontend deployments.
    @GetMapping({"/offers", "/offer-letters", "/offerletters"})
    public ResponseEntity<Map<String, Object>> listRecent() {
        return ResponseEntity.ok(service.listRecent());
    }

    @GetMapping({"/offers/{id}", "/offer-letters/{id}", "/offerletters/{id}"})
    public ResponseEntity<Map<String, Object>> getOffer(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOffer(id));
    }

    // Appraisal-style search route and aliases. Supports q, name and query parameters.
    @GetMapping({
        "/offers/search",
        "/search-offers",
        "/offer-letters/search",
        "/offerletters/search"
    })
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "query", required = false) String query) {
        String term = (q != null && !q.isBlank()) ? q.trim()
                : (name != null && !name.isBlank()) ? name.trim()
                : (query != null ? query.trim() : "");
        return ResponseEntity.ok(term.isBlank() ? service.listRecent() : service.search(term));
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>(service.health());
        result.put("apiVersion", "offerletter-history-v3");
        result.put("historyRoutes", List.of(
                "/api/offers",
                "/api/offers/search?q=",
                "/api/offer-letters",
                "/api/offer-letters/search?q="
        ));
        return result;
    }
}
