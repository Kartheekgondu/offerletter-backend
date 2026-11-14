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

    @PostMapping(value="/save-offer", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> save(
            @RequestParam Map<String,String> fields,
            @RequestPart(value="pdf", required=false) MultipartFile pdf) throws Exception {
        return ResponseEntity.ok(service.save(fields,pdf));
    }

    @PostMapping(value="/send-offer", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> send(
            @RequestParam Map<String,String> fields,
            @RequestPart(value="pdf", required=false) MultipartFile pdf) throws Exception {
        return ResponseEntity.ok(service.send(fields,pdf));
    }

    @GetMapping("/offers")
    public ResponseEntity<Map<String,Object>> listRecent() {
        return ResponseEntity.ok(service.listRecent());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<Map<String,Object>> getOffer(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOffer(id));
    }

    // Both legacy and Appraisal-style search routes are supported.
    @GetMapping({"/search-offers", "/offers/search"})
    public ResponseEntity<Map<String,Object>> search(
            @RequestParam(name="name", required=false) String name,
            @RequestParam(name="q", required=false) String q) {
        String term = (name != null && !name.isBlank()) ? name.trim()
                : (q != null ? q.trim() : "");
        if (term.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", true, "results", List.of()));
        }
        return ResponseEntity.ok(service.search(term));
    }

    @GetMapping("/health")
    public Map<String,Object> health() {
        return service.health();
    }
}
