package com.example.genaiidemo.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PdfController {

    @PostMapping("/check")
    public ResponseEntity<String> checkPdf(@RequestParam("file") MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            int pageCount = document.getNumberOfPages();
            System.out.println("✅ PDF uploaded with " + pageCount + " pages.");
            return ResponseEntity.ok("✅ PDF is valid. Pages: " + pageCount);
        } catch (IOException e) {
            System.err.println("❌ Failed to read PDF: " + e.getMessage());
            return ResponseEntity.badRequest().body("❌ Invalid PDF: " + e.getMessage());
        }
    }
}
