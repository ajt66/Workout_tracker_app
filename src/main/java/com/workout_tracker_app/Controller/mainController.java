package com.workout_tracker_app.Controller;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.programLog.Tag;
import com.workout_tracker_app.Entity.Program.Status;

import java.util.Optional;
import java.util.stream.Collectors;

import com.workout_tracker_app.Entity.User;
import com.workout_tracker_app.Repository.DailyLogRepo;
import com.workout_tracker_app.Repository.Exerciserepo;
import com.workout_tracker_app.Repository.UserRepository;
import com.workout_tracker_app.Repository.programLogRepo;
import com.workout_tracker_app.Service.authService;
import com.workout_tracker_app.dto.AnalyzeDTO;
import com.workout_tracker_app.dto.AuthRequest;
import com.workout_tracker_app.dto.AuthResponse;
import com.workout_tracker_app.dto.DLDTO;
import com.workout_tracker_app.dto.EntryDTO;
import com.workout_tracker_app.dto.RegisterRequest;
import com.workout_tracker_app.dto.programDTO;
import com.workout_tracker_app.Entity.Program.Status;
import com.workout_tracker_app.Entity.DailyLog.logStatus;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

import com.workout_tracker_app.Repository.programRepo;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@SessionAttributes({ "User", "User-id" })
@RequestMapping("/api")
@RequiredArgsConstructor
public class mainController {
    private final UserRepository userRepo;
    private final Exerciserepo exerciserepo;
    private final programRepo programrepo;
    private final programLogRepo pLogRepo;
    private final DailyLogRepo dLogRepo;
    private final authService AService;
    private Map<String, List<programDTO>> currentProgram = new HashMap();
    private String programName = "";
    private Long programid;

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

            return "redirect:/main";
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

            return "redirect:/main";
        } catch (Exception e) {
            model.addAttribute("error", "Could not authenticate user: " + e.getMessage());
            return "login";
        }

    }

    @GetMapping("/Main")
    public String showMain(Model model) {
        // Get the authenticated user's username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepo.findUserId(username).getId();
        setActiveProgramDetails(userId);
        
        dLogRepo.copyProgramDataForToday(userId);
        model.addAttribute("User", userRepo.findByUsername(username));
        model.addAttribute("User-id", userId);
        return "main";
    }

    @GetMapping("/Logs")
    public String showDailyLog(@ModelAttribute("User-id") Long userid, Model model) {
        EntryDTO dto = new EntryDTO();
        dto.setLists();
        model.addAttribute("logstatus", logStatus.values());
        model.addAttribute("curProgram", dLogRepo.findByCurrentDate(userid));
        model.addAttribute("curdate", LocalDate.now());
        model.addAttribute("dlp-name", programName);
        model.addAttribute("program-edit", dto);
        return "dailyLog";
    }

    @HxRequest
    @GetMapping("/Logs/date")
    public String showDailyLogDate(@ModelAttribute("User-id") Long userid, @RequestParam("date") LocalDate date,
            Model model) {
        model.addAttribute("Program", dLogRepo.findByDate(userid, date));
        model.addAttribute("date", date);
        return "dailyLog :: new-date";
    }

    @HxRequest
    @GetMapping("/Logs/edit")
    public String showDailyLogEditable(@ModelAttribute("User-id") Long userid) {
        return "fragments/Tables/new-row :: add-dl-row";
    }

    @PostMapping("/Logs/edit")
    public String showDailyLogPostEdit(@ModelAttribute("User-id") Long userid, @ModelAttribute("edit") EntryDTO edit,
            Model model) {

        return "redirect:/dailyLog";
    }

    @SuppressWarnings("null")
    @GetMapping("/Programs")
    public String showPrograms(@ModelAttribute("User") Optional<User> user, @ModelAttribute("User-id") Long userid,
            Model model) {
        model.addAttribute("currentProgram", currentProgram);
        model.addAttribute("programname", programName);
        model.addAttribute("programId", programid);

        List<programDTO> itres;
        Map<String, List<programDTO>> inactiveGroup;
        List<programDTO> ipstat = programrepo.findByInactiveStatus(Status.valueOf("inactive"), userid);
        Iterator<programDTO> iterator = ipstat.iterator();
        Map<String, Map<String, List<programDTO>>> finalData = new HashMap<>();
        while (iterator.hasNext()) {
            String progname = iterator.next().getProgram_name();
            itres = pLogRepo.findByProgramNamewithDetails(progname, userid);
            inactiveGroup = itres.stream()
                    .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList()));
            finalData.put(progname, inactiveGroup);
        }

        EntryDTO dto = new EntryDTO();
        dto.setLists();
        model.addAttribute("tag", Tag.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("otherProgram", finalData);
        model.addAttribute("program", dto);
        model.addAttribute("days", List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
        return "Programs";
    }

    @HxRequest
    @GetMapping("/Programs/switch")
    public String showProgramSwitch(@RequestParam("progname") String progname, @ModelAttribute("User-id") Long userid) {
        programDTO program = programrepo.findProgramId(progname, userid);
        programrepo.updateProgramStatus(Status.valueOf("active"), program.getProgram_id(), userid);
        programrepo.updateProgramStatus(Status.valueOf("inactive"), programid, userid);
        setActiveProgramDetails(userid);
        return "redirect:/Programs";
    }

    @HxRequest
    @PostMapping(value = "/Programs/delete")
    public String deleteProgram(@RequestParam("progname") String progname, @ModelAttribute("User-id") Long userid,
            Model model) {
        programDTO program = programrepo.findProgramId(progname, userid);
        programrepo.deleteById(program.getProgram_id());
            return "redirect:/Programs";
        }

    @HxRequest
    @PostMapping(value = "/Programs/popup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String showProgramPopup(@RequestParam("progname") String progname, @ModelAttribute("User-id") Long userid,
            Model model) {
        model.addAttribute("popup-Program", pLogRepo.findByProgramNamewithDetails(progname, userid).stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList())));
        model.addAttribute("prog-name", progname);
        return "fragments/popups/Prog-edit :: Program-edit";
    }

    @PostMapping(value = "/Programs/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsEdit(@ModelAttribute("program") EntryDTO program, Model model) {
        return "redirect:/Programs";
    }

    @HxRequest
    @GetMapping("/Programs/add-row")
    public String showProgramsEditable() {
        return "fragments/Tables/new-row :: add-prog-row";
    }

    @PostMapping(value = "/Programs/Add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsAdd(@ModelAttribute("program") EntryDTO program, Model model) {
        return "redirect:/Programs";
    }

    @GetMapping("/Exercise")
    public String showExercise(Model model) {
        model.addAttribute("ExerciseList", new ExerciseList());
        model.addAttribute("category", exerciserepo.findByAllCategory());
        model.addAttribute("muscle_group", exerciserepo.findByAllMuscleGroup());
        return "Exercise";
    }

    @PostMapping(path = "/Exercise", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showExercisePost(Model model, @ModelAttribute("ExerciseList") ExerciseList exerlist) {
        exerciserepo.save(exerlist);
        return "redirect:/Exercise";
    }

    @GetMapping("/Analyze")
    public String showAnalyze(@ModelAttribute("User-id") Long userid, Model model) {
        model.addAttribute("exercisesum",
                dLogRepo.sumByExerciseAndStatus(userid, logStatus.valueOf("completed")));
        model.addAttribute("SCsum", dLogRepo.sumByStatus(userid, logStatus.valueOf("completed")));
        model.addAttribute("SPsum", dLogRepo.sumByStatus(userid, logStatus.valueOf("pending")));
        model.addAttribute("SIsum", dLogRepo.sumByStatus(userid, logStatus.valueOf("cancelled")));
        model.addAttribute("volumesum", dLogRepo.sumByVolume(userid));
        model.addAttribute("WSRsum", dLogRepo.TotalWSR(userid));
        return "Analyze";
    }

    @SuppressWarnings("null")
    @HxRequest
    @GetMapping("/Analyze/workouts")
    public String showPreWorkouts(@ModelAttribute("User-id") Long userid,
            @RequestParam(value = "category") String category,
            Model model) {
        List<AnalyzeDTO> preList = dLogRepo.WorkoutsTabPlainQuery(8, userid);
        model.addAttribute("preworklabels", preList.stream().filter(item -> item.getDates() != null)
                .map(AnalyzeDTO::getDates).collect(Collectors.toList()));
        model.addAttribute("preworkvolume", preList.stream().filter(item -> item.getTotal_volume() != null)
                .map(AnalyzeDTO::getTotal_volume).collect(Collectors.toList()));
        model.addAttribute("preworkweights", preList.stream().filter(item -> item.getTotal_weight() != null)
                .map(AnalyzeDTO::getTotal_weight).collect(Collectors.toList()));
        model.addAttribute("preworksets", preList.stream().filter(item -> item.getTotal_sets() != null)
                .map(AnalyzeDTO::getTotal_sets).collect(Collectors.toList()));
        model.addAttribute("preworkreps", preList.stream().filter(item -> item.getTotal_reps() != null)
                .map(AnalyzeDTO::getTotal_reps).collect(Collectors.toList()));
        return "fragments/Charts/workChart :: workChartjs";
    }

    @SuppressWarnings("null")
    @HxRequest
    @PostMapping("/Analyze/workouts")
    public String showPostWorkouts(@ModelAttribute("User-id") Long userid,
            @RequestParam(value = "category") String category,
            Model model) {
        if (category.equals("cardio") || category.equals("isometrics") || category.equals("flexibility")) {
            model.addAttribute("workResult", "No data available for this category.");
        }

        List<AnalyzeDTO> postList = dLogRepo.WorkoutsTabCategoryQuery(8, category, userid);
        model.addAttribute("postworklabels", postList.stream().filter(item -> item.getDates() != null)
                .map(AnalyzeDTO::getDates).collect(Collectors.toList()));
        model.addAttribute("postworkvolume", postList.stream().filter(item -> item.getTotal_volume() != null)
                .map(AnalyzeDTO::getTotal_volume).collect(Collectors.toList()));
        model.addAttribute("postworkweights", postList.stream().filter(item -> item.getTotal_weight() != null)
                .map(AnalyzeDTO::getTotal_weight).collect(Collectors.toList()));
        model.addAttribute("postworksets", postList.stream().filter(item -> item.getTotal_sets() != null)
                .map(AnalyzeDTO::getTotal_sets).collect(Collectors.toList()));
        model.addAttribute("postworkreps", postList.stream().filter(item -> item.getTotal_reps() != null)
                .map(AnalyzeDTO::getTotal_reps).collect(Collectors.toList()));
        return "fragments/Charts/workChart :: workChartjs";
    }

    @HxRequest
    @GetMapping("/Analyze/breakdown")
    public String showPreBreakdown(@ModelAttribute("User-id") Long userid,
            @RequestParam(value = "filter") String filter,
            Model model) {
        List<Object[]> preList = dLogRepo.BreakdownTabQuery(8,
                userid);
        List<String> muscleGroups = new ArrayList<>();
        List<Long> totalWeights = new ArrayList<>();
        List<Long> totalSets = new ArrayList<>();
        List<Long> totalReps = new ArrayList<>();
        List<Long> totalVolume = new ArrayList<>();
        for (Object[] row : preList) {
            muscleGroups.add((String) row[0]);
            totalWeights.add((Long) row[1]);
            totalSets.add((Long) row[2]);
            totalReps.add((Long) row[3]);
            totalVolume.add((Long) row[4]);
        }
        model.addAttribute("breakmuscle", muscleGroups);
        model.addAttribute("breakWeights", totalWeights);
        model.addAttribute("breakSets", totalSets);
        model.addAttribute("breakReps", totalReps);
        model.addAttribute("breakVolume", totalVolume);
        return "fragments/Charts/breakChart :: breakChartjs";
    }

    /*
     * @HxRequest
     * 
     * @PostMapping("/Analyze/breakdown")
     * public String showPostBreakdown(@ModelAttribute("User-id") User userid,
     * 
     * @RequestParam(value = "filter") String filter,
     * Model model) {
     * List<Object[]> postList =
     * dLogRepo.BreakdownTabQuery(Calendar.getInstance().get(Calendar.MONTH) + 1,
     * userid.getId());
     * List<String> muscleGroups = new ArrayList<>();
     * List<Long> totalWeights = new ArrayList<>();
     * List<Long> totalSets = new ArrayList<>();
     * List<Long> totalReps = new ArrayList<>();
     * for (Object[] row : postList) {
     * muscleGroups.add((String) row[0]);
     * totalWeights.add((Long) row[1]);
     * totalSets.add((Long) row[2]);
     * totalReps.add((Long) row[3]);
     * }
     * model.addAttribute("postbreakmuscle", muscleGroups);
     * model.addAttribute("postbreakWeights", totalWeights);
     * model.addAttribute("postbreakSets", totalSets);
     * model.addAttribute("postbreakReps", totalReps);
     * return "Analyze :: breakChart";
     * }
     */

    @HxRequest
    @GetMapping("/Analyze/exercise")
    public String showPreExercises(Model model, @ModelAttribute("User-id") Long userid) {
        List<AnalyzeDTO> postList = dLogRepo.ExerciseTabDates(8, userid);
        model.addAttribute("exerciseDates", postList.stream().filter(item -> item.getDates() != null)
                .map(AnalyzeDTO::getDates).collect(Collectors.toList()));
        return "fragments/Charts/exerciseChart :: exerciseChartjs";
    }

    @HxRequest
    @PostMapping("/Analyze/exercise")
    public String showPostExercises(@ModelAttribute("User-id") Long userid,
            @RequestParam(value = "exercise") String exercise,
            Model model) {
        List<Object[]> preList = dLogRepo.ExerciseTabQuery(8, exercise, userid);
        List<String> exerciseDates = new ArrayList<>();
        List<Long> totalWeights = new ArrayList<>();
        List<Long> totalSets = new ArrayList<>();
        List<Long> totalReps = new ArrayList<>();
        List<Long> totalVolume = new ArrayList<>();
        for (Object[] row : preList) {
            exerciseDates.add((String) row[0]);
            totalWeights.add((Long) row[1]);
            totalSets.add((Long) row[2]);
            totalReps.add((Long) row[3]);
            totalVolume.add((Long) row[4]);
        }
        model.addAttribute("exerciseDates", exerciseDates);
        model.addAttribute("exerciseWeights", totalWeights);
        model.addAttribute("exerciseSets", totalSets);
        model.addAttribute("exerciseReps", totalReps);
        model.addAttribute("exerciseVolume", totalVolume);
        return "fragments/Charts/exerciseChart :: exerciseChartjs";
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

    public void setActiveProgramDetails(Long userid) {
        programDTO pstat = programrepo.findByActiveStatus(Status.valueOf("active"), userid);
        programName = pstat.getProgram_name();
        programid = programrepo.findProgramId(programName, userid).getProgram_id();
        List<programDTO> pres = pLogRepo.findByProgramNamewithDetails(programName, userid);
        currentProgram = pres.stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList()));
    }

}
