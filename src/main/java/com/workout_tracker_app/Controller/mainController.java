package com.workout_tracker_app.Controller;

import com.workout_tracker_app.Repository.UserRepository;
import com.workout_tracker_app.Service.authService;
import com.workout_tracker_app.dto.AuthRequest;
import com.workout_tracker_app.dto.AuthResponse;
import com.workout_tracker_app.dto.RegisterRequest;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.security.Security;

import org.springframework.http.MediaType;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class mainController {
    private final authService AService;
    private final UserRepository userRepo;

    @GetMapping("/home")
    public String show() {
        return "home";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping(path = "/register/main", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showRegisterMain(@ModelAttribute("registerRequest") @Nullable RegisterRequest request,
            Model model, HttpServletResponse response) throws Exception {
        // Register new user and return JWT
        try {
            AuthResponse service = AService.register(request);
            Cookie cookie = new Cookie("jwt", service.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setSecure(true); // Ensure the cookie is sent over HTTPS
            response.addCookie(cookie);

            String username = request.getUsername();
            model.addAttribute("User", userRepo.findByUsername(username));

            return "main";
        } catch (Exception e) {
            // Handle registration error (e.g., user already exists)
            model.addAttribute("error", "Could not register user: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "login";
    }

    @PostMapping(path = "/login/main", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showLoginMain(@ModelAttribute("authRequest") AuthRequest Request, Model model,
            HttpServletResponse response) {
        // Authenticate and return JWT
        try {
            AuthResponse service = AService.authenticate(Request);
            Cookie cookie = new Cookie("jwt", service.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setSecure(true);
            response.addCookie(cookie);

            String username = Request.getUsername();
            model.addAttribute("User", userRepo.findByUsername(username));
            return "main";
        } catch (Exception e) {
            model.addAttribute("error", "Could not authenticate user: " + e.getMessage());
            return "login";
        }

    }

    @GetMapping("/Main")
    public String showMain(Model model) {
        // Get the authenticated user's username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("User", userRepo.findByUsername(username));
        return "main";
    }

    @GetMapping("/Schedule")
    public String showSchedule(Model model) {
        return "Schedule";
    }

    @GetMapping("/Programs")
    public String showPrograms(Model model) {
        return "Programs";
    }

    @GetMapping("/Exercise")
    public String showExercise(Model model) {
        return "Exercise";
    }

    @GetMapping("/Analyze")
    public String showAnalyze(Model model) {
        return "Analyze";
    }

    @GetMapping("/sign-out")
    public String showSignOut(HttpServletResponse response) {
        // Clear the JWT cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire the cookie immediately
        cookie.setSecure(true);
        response.addCookie(cookie);

        return "sign-out"; // Redirect to login page after sign-out
    }
}
