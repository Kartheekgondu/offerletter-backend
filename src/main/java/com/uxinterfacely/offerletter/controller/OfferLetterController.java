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
    public OfferLetterController(OfferLetterService service){this.service=service;}

    @PostMapping(value="/save-offer", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> save(@RequestParam Map<String,String> fields,
            @RequestPart(value="pdf", required=false) MultipartFile pdf) throws Exception {
        return ResponseEntity.ok(service.save(fields,pdf));
    }

    @PostMapping(value="/send-offer", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> send(@RequestParam Map<String,String> fields,
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

    @GetMapping("/search-offers")
    public ResponseEntity<Map<String,Object>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String name) {
        String query = (q != null) ? q.trim() : (name == null ? "" : name.trim());
        if (query.isEmpty()) {
            throw new IllegalArgumentException("Enter a name, email, job title, department, location or record ID to search.");
        }
        return ResponseEntity.ok(service.search(query));
    }

    @GetMapping("/health")
    public Map<String,Object> health(){return service.health();}
}
