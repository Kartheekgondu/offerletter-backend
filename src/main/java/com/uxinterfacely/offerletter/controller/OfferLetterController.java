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

    @GetMapping("/offers")
    public ResponseEntity<Map<String, Object>> listRecent() {
        return ResponseEntity.ok(service.listRecent());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<Map<String, Object>> getOffer(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOffer(id));
    }

    // Appraisal-style route, plus backwards-compatible legacy route.
    @GetMapping({"/offers/search", "/search-offers", "/offer-letters/search"})
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "query", required = false) String query) {
        String term = (q != null && !q.isBlank()) ? q.trim()
                : (name != null && !name.isBlank()) ? name.trim()
                : (query != null ? query.trim() : "");
        return ResponseEntity.ok(service.search(term));
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>(service.health());
        result.put("apiVersion", "offerletter-search-v2");
        result.put("searchRoutes", List.of("/api/offers/search?q=", "/api/search-offers?name=", "/api/offer-letters/search?query="));
        return result;
    }
}
