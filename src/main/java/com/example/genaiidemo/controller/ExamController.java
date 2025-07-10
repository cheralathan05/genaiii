package com.example.genaiidemo.controller;

import com.example.genaiidemo.ato.ExamRequest;
import com.example.genaiidemo.model.Exam;
import com.example.genaiidemo.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*") // Allow all origins for frontend access
public class ExamController {

    @Autowired
    private ExamService examService;

    // ✅ 1. Generate exam manually
    @PostMapping("/generate")
    public ResponseEntity<Exam> generateExam(@RequestBody ExamRequest request) {
        Exam generated = examService.generateExam(request);
        return ResponseEntity.ok(generated);
    }

    // ✅ 2. Upload PDF and generate exam
    @PostMapping("/upload-unit")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file,
                                            @RequestParam("email") String email,
                                            @RequestParam("marks1") int mark1,
                                            @RequestParam("marks2") int mark2,
                                            @RequestParam("marks5") int mark5) {
        try {
            // Default values for subject/grade/examType/difficulty/topic
            examService.processPdfAndGenerateExam(
                    file, email,
                    "AutoSubject", "10", "Final", "medium",
                    List.of("General"), mark1, mark2, mark5
            );
            return ResponseEntity.ok("✅ PDF processed and exam generated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }

    // ✅ 3. Fetch exam history by user email
    @GetMapping("/user/history")
    public ResponseEntity<List<Exam>> getUserHistory(@RequestParam String email) {
        List<Exam> exams = examService.getExamHistoryByEmail(email);
        return ResponseEntity.ok(exams);
    }

    // ✅ 4. Download exam PDF by exam ID
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = examService.generatePdf(id);
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("exam_" + id + ".pdf").build());
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
