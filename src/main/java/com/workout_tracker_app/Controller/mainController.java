package com.workout_tracker_app.Controller;

import com.workout_tracker_app.Service.authService;
import com.workout_tracker_app.dto.AuthRequest;
import com.workout_tracker_app.dto.AuthResponse;
import com.workout_tracker_app.dto.RegisterRequest;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class mainController {
    private final authService AService;

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
    public String showRegisterMain(@ModelAttribute("registerRequest") RegisterRequest request,
            HttpServletResponse response) throws Exception {
        // Register new user and return JWT
        try {
            AuthResponse service = AService.register(request);
            Cookie cookie = new Cookie("jwt", service.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setSecure(true); // Ensure the cookie is sent over HTTPS
            response.addCookie(cookie);

            return "main";
        } catch (Exception e) {
            // Handle registration error (e.g., user already exists)
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "login";
    }

    @PostMapping(path = "/login/main", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showLoginMain(@ModelAttribute("authRequest") AuthRequest Request,
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

            return "main";
        } catch (Exception e) {
            // Handle authentication error (e.g., invalid credentials)
            return "login";
        }

    }

}
