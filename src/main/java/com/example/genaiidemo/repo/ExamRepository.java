package com.example.genaiidemo.repo;

import com.example.genaiidemo.model.Exam;
import com.example.genaiidemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Find all exams created by a specific user
    List<Exam> findByUser(User user);

    // (Optional) Find exams by user and subject
    List<Exam> findByUserAndSubject(User user, String subject);

    // (Optional) Get recent 10 exams by user
    List<Exam> findTop10ByUserOrderByCreatedAtDesc(User user);
}
