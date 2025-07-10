package com.example.genaiidemo.repo;

import com.example.genaiidemo.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository  extends JpaRepository<Question, Long> {
}
