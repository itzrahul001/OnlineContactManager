package com.example.OnlineContactManager.controller;

import com.example.OnlineContactManager.dao.UserRepository;
import com.example.OnlineContactManager.models.User;
import com.example.OnlineContactManager.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.Random;

@Controller
public class ForgotController {


    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    // email id form open handler
    @RequestMapping("/forgot")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {
        //generating otp
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000;
        System.out.println("Email: " + email);
        System.out.println("OTP: " + otp);
        String subject = "OTP from SCM";
        String message = """
                <div style="
                    font-family: Arial, sans-serif;
                    color: #333;
                    background: #f9f9f9;
                    padding: 20px;
                    border: 1px solid #ddd;
                    border-radius: 10px;
                    width: fit-content;
                    margin: auto;
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                ">
                    <h1 style="
                        color: #4CAF50;
                        font-size: 24px;
                        margin-bottom: 10px;
                    ">
                        Hello User,
                    </h1>
                    <p style="
                        font-size: 18px;
                        color: #555;
                        margin-bottom: 15px;
                    ">
                        Your OTP is:
                    </p>
                    <b style="
                        display: inline-block;
                        font-size: 20px;
                        color: #000;
                        padding: 10px 15px;
                        background: #e0f7fa;
                        border-radius: 5px;
                        letter-spacing: 2px;
                    ">
                """ + otp + """
                    </b>
                    <p style="
                        margin-top: 15px;
                        font-size: 16px;
                        color: #888;
                    ">
                        Thanks for using our service!
                    </p>
                </div>
                """;

        String to = email;

        boolean isSent = this.emailService.sendEmail(subject, message, to);
        if (isSent) {
            //send otp to user email and return verify_otp
            session.setAttribute("myotp", otp);
            session.setAttribute("email", email);
            return "verify_otp";
        } else {
            session.setAttribute("message", "Failed to send OTP");
            return "forgot_email_form";
        }


    }

    @PostMapping("/verify_otp")
    public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {
        int myOtp = (int) session.getAttribute("myotp");

        String email = (String) session.getAttribute("email");

        if (myOtp == otp) {

            User user = this.userRepository.getUserByUsername(email);
            if (user == null) {
                session.setAttribute("message", "You are not a registered user");
                return "forgot_email_form";
            } else {

                return "password_change_form";

            }


        } else {
            session.setAttribute("message", "You have entered wrong OTP");
            return "verify_otp";
        }

    }





    @PostMapping("/change_password")
    public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = this.userRepository.getUserByUsername(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(user);
        return "login";
    }
}
