package com.example.genaiidemo.service;

import com.example.genaiidemo.ato.ExamRequest;
import com.example.genaiidemo.model.Exam;
import com.example.genaiidemo.model.Question;
import com.example.genaiidemo.model.User;
import com.example.genaiidemo.repo.ExamRepository;
import com.example.genaiidemo.repo.QuestionRepository;
import com.example.genaiidemo.repo.UserRepository;
import com.example.genaiidemo.server.QuestionGeneratorService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExamService {

    @Autowired
    @Qualifier("openAiService")
    private QuestionGeneratorService aiService;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public Exam generateExam(ExamRequest request) {
        Exam exam = new Exam();
        exam.setSubject(request.getSubject());
        exam.setGradeLevel(request.getGradeLevel());
        exam.setExamType(request.getExamType());
        exam.setCreatedAt(LocalDateTime.now());

        User user = userRepository.findByEmail(request.getEmail());
        exam.setUser(user);

        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < request.getMarks1(); i++) {
            for (String topic : request.getTopics()) {
                String qText = aiService.generateQuestionFromText("1-mark question on " + topic, "1");
                questions.add(new Question(qText, "knowledge", topic, request.getDifficulty(), exam));
            }
        }

        for (int i = 0; i < request.getMarks2(); i++) {
            for (String topic : request.getTopics()) {
                String qText = aiService.generateQuestionFromText("2-mark question on " + topic, "2");
                questions.add(new Question(qText, "comprehension", topic, request.getDifficulty(), exam));
            }
        }

        for (int i = 0; i < request.getMarks5(); i++) {
            for (String topic : request.getTopics()) {
                String qText = aiService.generateQuestionFromText("5-mark question on " + topic, "5");
                questions.add(new Question(qText, "application", topic, request.getDifficulty(), exam));
            }
        }

        exam.setQuestions(questions);
        examRepository.save(exam);
        questionRepository.saveAll(questions);
        return exam;
    }

    public void processPdfAndGenerateExam(MultipartFile file, String email, String subject, String gradeLevel,
                                          String examType, String difficulty, List<String> topics,
                                          int mark1, int mark2, int mark5) throws IOException {

        PDDocument document = PDDocument.load(file.getInputStream());
        String text = new PDFTextStripper().getText(document);
        document.close();

        List<Question> allQuestions = new ArrayList<>();

        for (int i = 0; i < mark1; i++) {
            String qText = aiService.generateQuestionFromText(text, "1");
            allQuestions.add(new Question(qText, "knowledge", topics.get(0), difficulty, null));
        }

        for (int i = 0; i < mark2; i++) {
            String qText = aiService.generateQuestionFromText(text, "2");
            allQuestions.add(new Question(qText, "comprehension", topics.get(0), difficulty, null));
        }

        for (int i = 0; i < mark5; i++) {
            String qText = aiService.generateQuestionFromText(text, "5");
            allQuestions.add(new Question(qText, "application", topics.get(0), difficulty, null));
        }

        Exam exam = new Exam(subject, gradeLevel, examType, LocalDateTime.now(), userRepository.findByEmail(email), allQuestions);
        allQuestions.forEach(q -> q.setExam(exam));

        examRepository.save(exam);
        questionRepository.saveAll(allQuestions);
    }

    public List<Exam> getExamHistoryByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null ? examRepository.findByUser(user) : new ArrayList<>();
    }

    public byte[] generatePdf(Long id) {
        Exam exam = examRepository.findById(id).orElse(null);
        if (exam == null) return null;

        try {
            Document doc = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            doc.add(new Paragraph("📘 Subject: " + exam.getSubject()));
            doc.add(new Paragraph("🎓 Grade: " + exam.getGradeLevel()));
            doc.add(new Paragraph("📝 Type: " + exam.getExamType()));
            doc.add(new Paragraph("🕒 Created At: " + exam.getCreatedAt()));
            doc.add(new Paragraph("\n🔹 Questions:\n"));

            int count = 1;
            for (Question q : exam.getQuestions()) {
                doc.add(new Paragraph(count++ + ". " + q.getText()));
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
