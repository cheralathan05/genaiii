package com.example.genaiidemo.server;

import com.example.genaiidemo.model.User;
import com.example.genaiidemo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private MailService mailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ 1. Signup logic with OTP
    public String signUp(String name, String email, String password) {
        if (repo.findByEmail(email) != null) {
            return "User already exists";
        }

        String hashedPwd = passwordEncoder.encode(password);
        String code = generateOTP();

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPwd);
        user.setConfirmationCode(code);
        user.setVerified(false);

        repo.save(user);
        mailService.sendConfirmationCode(email, code);

        return "Signup successful. Check your email for the confirmation code.";
    }

    // ✅ 2. Verify OTP
    public String verifyCode(String email, String code) {
        User user = repo.findByEmail(email);
        if (user == null || !code.equals(user.getConfirmationCode())) {
            return "Invalid confirmation code";
        }

        user.setVerified(true);
        user.setConfirmationCode(null);
        repo.save(user);

        return "Email verified successfully!";
    }

    // ✅ 3. Login
    public String login(String email, String password) {
        User user = repo.findByEmail(email);
        if (user == null) return "You need to sign up first";
        if (!user.isVerified()) return "Please verify your email first";

        boolean match = passwordEncoder.matches(password, user.getPassword());
        if (!match) return "Incorrect password";

        return "Login successful! Welcome " + user.getName();
    }

    // ✅ 4. Send OTP to existing verified user
    public String sendOtpIfUserExists(String email, String password) {
        User user = repo.findByEmail(email);
        if (user == null) return "You need to sign up first";

        boolean match = passwordEncoder.matches(password, user.getPassword());
        if (!match) return "Incorrect password";

        if (!user.isVerified()) return "Please verify your email first";

        String code = generateOTP();
        user.setConfirmationCode(code);
        repo.save(user);

        mailService.sendConfirmationCode(email, code);
        return "OTP sent successfully to your email.";
    }

    // ✅ 5. Utility method to generate 6-digit OTP
    private String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
