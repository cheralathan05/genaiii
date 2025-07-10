package com.example.genaiidemo.controller;

import com.example.genaiidemo.server.MailService;
import com.example.genaiidemo.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.signUp(
                request.get("name"),
                request.get("email"),
                request.get("password")
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.verifyCode(
                request.get("email"),
                request.get("code")
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.login(
                request.get("email"),
                request.get("password")
        ));
    }

    @PostMapping("/test-mail")
    public String testMail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code"); // or generate a random one here

        mailService.sendConfirmationCode(email, code);
        return "Test email sent to: " + email;
    }
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        return ResponseEntity.ok(userService.sendOtpIfUserExists(email, password));
    }



}
