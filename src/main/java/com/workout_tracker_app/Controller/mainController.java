package com.workout_tracker_app.Controller;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.programLog.Tag;
import com.workout_tracker_app.Entity.Program.Status;

import java.util.Optional;
import java.util.stream.Collectors;

import com.workout_tracker_app.Entity.User;
import com.workout_tracker_app.Repository.Exerciserepo;
import com.workout_tracker_app.Repository.UserRepository;
import com.workout_tracker_app.Repository.programLogRepo;
import com.workout_tracker_app.Service.authService;
import com.workout_tracker_app.dto.AuthRequest;
import com.workout_tracker_app.dto.AuthResponse;
import com.workout_tracker_app.dto.RegisterRequest;
import com.workout_tracker_app.dto.programDTO;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

import com.workout_tracker_app.Repository.programRepo;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class mainController {
    private final authService AService;
    private final UserRepository userRepo;
    private final Exerciserepo exerciserepo;
    private final programRepo programrepo;
    private final programLogRepo pLogRepo;

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
        model.addAttribute("User-id", userRepo.findUserId(username));
        return "main";
    }

    @GetMapping("/Schedule")
    public String showSchedule(Model model) {
        return "Schedule";
    }

    @GetMapping("/Programs")
    public String showPrograms(@ModelAttribute("User") Optional<User> user, @ModelAttribute("User-id") User userid,
            Model model) {
        String id = String.valueOf(userid.getId());
        programDTO pstat = programrepo.findByActiveStatus(id);
        List<programDTO> pres = pLogRepo.findByProgramNamewithDetails(pstat.getProgram_name(), id);
        Map<String, List<programDTO>> groupedData = pres.stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList()));
        model.addAttribute("currentProgram", groupedData);
        model.addAttribute("programname", pstat.getProgram_name());

        List<programDTO> itres;
        Map<String, List<programDTO>> inactiveGroup;
        List<programDTO> ipstat = programrepo.findByInactiveStatus(id);
        Iterator<programDTO> iterator = ipstat.iterator();
        Map<String, Map<String, List<programDTO>>> finalData = new HashMap<>();
        while (iterator.hasNext()) {
            String progname = iterator.next().getProgram_name();
            itres = pLogRepo.findByProgramNamewithDetails(progname, id);
            inactiveGroup = itres.stream()
                    .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList()));
            finalData.put(progname, inactiveGroup);
        }

        model.addAttribute("tag", Tag.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("otherProgram", finalData);
        model.addAttribute("program", new HashMap<String, List<programDTO>>());
        return "Programs";
    }

    @HxRequest
    @GetMapping(value = "/Programs/edit")
    public String showProgramEdit(@RequestParam("progname") String name,
            @ModelAttribute("otherProgram") Map<String, Map<String, List<programDTO>>> mapData, Model model) {
        model.addAttribute("selectedprog", name);
        model.addAttribute("progedit", mapData.get(name));
        return "Programs :: Program-edit";
    }

    @PostMapping(value = "/Programs", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsPost(@ModelAttribute("program") programDTO program, Model model) {
        return "redirect:/Programs";
    }

    @GetMapping("/Exercise")
    public String showExercise(Model model) {
        List<ExerciseList> mgResult = exerciserepo.findByAllMuscleGroup();
        List<ExerciseList> cResult = exerciserepo.findByAllCategory();
        model.addAttribute("ExerciseList", new ExerciseList());
        model.addAttribute("category", cResult);
        model.addAttribute("muscle_group", mgResult);
        return "Exercise";
    }

    @PostMapping(path = "/Exercise", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showExercisePost(Model model, @ModelAttribute("ExerciseList") ExerciseList exerlist) {
        exerciserepo.save(exerlist);
        return "redirect:/Exercise";
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
