package com.example.genaiidemo.repo;

import com.example.genaiidemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ Used for login, signup, and linking exams to users
    User findByEmail(String email);

    // ✅ To check if a user is already registered (e.g., during signup)
    boolean existsByEmail(String email);
}
