package com.example.OnlineContactManager.controller;


import com.example.OnlineContactManager.dao.UserRepository;
import com.example.OnlineContactManager.helper.Message;
import com.example.OnlineContactManager.models.Contact;
import com.example.OnlineContactManager.models.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Online Contact Manager");

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Online Contact Manager");

        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model, HttpSession session) {
        model.addAttribute("title", "Signup - Online Contact Manager");
        model.addAttribute("user", new User());

        // Manually add session attribute
        if (session.getAttribute("message") != null) {
            model.addAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }

        return "signup";
    }


    @PostMapping("/do_register")
    public String registerUser(
            @Valid
            @ModelAttribute("user") User user,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,

            RedirectAttributes redirectAttributes) {

        try {
            if (!agreement) {

                throw new IllegalArgumentException("Please agree to the terms and conditions.");
            }


            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));


            userRepository.save(user);

            redirectAttributes.addFlashAttribute("message", new Message("Successfully Registered!", "alert-success"));
            return "redirect:/signup";
        } catch (Exception e) {
            model.addAttribute("user", user);
            redirectAttributes.addFlashAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
            return "redirect:/signup";
        }
    }


    @RequestMapping("/signin")
    public String customlogin(Model model) {
        model.addAttribute("title", "Login - Online Contact Manager");
        return "login";
    }





}
