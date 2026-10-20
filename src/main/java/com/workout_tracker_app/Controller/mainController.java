package com.workout_tracker_app.Controller;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.FragmentsRendering;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.programLog.program_tag;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.workout_tracker_app.Entity.User;
import com.workout_tracker_app.Repository.DailyLogRepo;
import com.workout_tracker_app.Repository.Exerciserepo;
import com.workout_tracker_app.Repository.UserRepository;
import com.workout_tracker_app.Repository.programLogRepo;
import com.workout_tracker_app.dto.AnalyzeDTO;
import com.workout_tracker_app.dto.EntryDTO;
import com.workout_tracker_app.dto.mapDTO;
import com.workout_tracker_app.dto.programDTO;
import com.workout_tracker_app.Entity.DailyLog.log_status;
import com.workout_tracker_app.Entity.Program.program_status;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpSession;

import com.workout_tracker_app.Repository.programRepo;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@SessionAttributes({ "User", "User_id" })
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class mainController {
    private final UserRepository userRepo;
    private final Exerciserepo exerciserepo;
    private final programRepo programrepo;
    private final programLogRepo pLogRepo;
    private final DailyLogRepo dLogRepo;
    private final HttpSession session;
    private int progedit, progadd, dledit;

    @ModelAttribute("User")
    public Optional<User> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username);
    }

    @ModelAttribute("User_id")
    public Long getUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findUserId(username);
    }

    @SuppressWarnings("null")
    public void setActiveProgram(Long userid) {
        programDTO pstat = programrepo.findByActiveStatus(program_status.valueOf("active").toString(), userid);
        List<programDTO> pres = pLogRepo.findByProgramNamewithDetails(pstat.getProgram_name(), userid);
        Map<String, List<programDTO>> currentProgram = pres.stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList()));

        if (session.getAttribute("Program") == null) {
            session.setAttribute("Program", currentProgram);
        } else if (session.getAttribute("Program") != null) {
            session.removeAttribute("Program");
            session.setAttribute("Program", currentProgram);
        }

    }

    @SuppressWarnings("unchecked")
    public Map<String, List<programDTO>> getActiveProgram() {
        return (Map<String, List<programDTO>>) session.getAttribute("Program");
    }

    public void setActiveProgramID(Long userid) {
        programDTO pstat = programrepo.findByActiveStatus(program_status.valueOf("active").toString(), userid);

        if (session.getAttribute("ProgramID") == null) {
            session.setAttribute("ProgramID",
                    programrepo.findProgramId(pstat.getProgram_name(), userid).getProgram_id());
        } else if (session.getAttribute("ProgramID") != null) {
            session.removeAttribute("ProgramID");
            session.setAttribute("ProgramID",
                    programrepo.findProgramId(pstat.getProgram_name(), userid).getProgram_id());
        }

    }

    public Long getActiveProgramID() {
        return (Long) session.getAttribute("ProgramID");
    }

    public void setActiveProgramName(Long userid) {
        programDTO pstat = programrepo.findByActiveStatus(program_status.valueOf("active").toString(), userid);

        if (session.getAttribute("ProgramName") == null) {
            session.setAttribute("ProgramName", pstat.getProgram_name());
        } else if (session.getAttribute("ProgramName") != null) {
            session.removeAttribute("ProgramName");
            session.setAttribute("ProgramName", pstat.getProgram_name());
        }
    }

    public String getActiveProgramName() {
        return (String) session.getAttribute("ProgramName");
    }

    public void setProgEdit(int count) {
        progedit = count;
    }

    public void ProgEditIncrement() {
        progedit++;
    }

    public void setProgAdd(int count) {
        progadd = count;
    }

    public void ProgAddIncrement() {
        progadd++;
    }

    public void setDLEdit(int count) {
        dledit = count;
    }

    public void DLEditIncrement() {
        dledit++;
    }

    @GetMapping("")
    public String showMain(Model model) {
        // Get the authenticated user's username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = getUserId();
        setActiveProgram(userId);
        setActiveProgramID(userId);
        setActiveProgramName(userId);

        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        dLogRepo.copyProgramDataForToday(userId, today);
        dLogRepo.changeStatus(userId);
        model.addAttribute("User_email", getUser().get().getEmail());
        model.addAttribute("User_name", username);
        model.addAttribute("User_id", userId);
        model.addAttribute("label", List.of("d1", "d2", "d3", "d4", "d5", "d6"));
        model.addAttribute("data", List.of(1, 2, 3, 4, 5, 6));
        return "main";
    }

    @GetMapping("/Logs")
    public String showDailyLog(@ModelAttribute("User_id") Long userid, Model model) {
        model.addAttribute("logstatus", log_status.values());
        model.addAttribute("curProgram", dLogRepo.findByCurrentDate(userid, getActiveProgramID()));
        model.addAttribute("curdate", LocalDate.now());
        model.addAttribute("dlp_name", getActiveProgramName());
        return "dailyLog";
    }

    @HxRequest
    @GetMapping("/Logs/date")
    public View showDailyLogDate(@ModelAttribute("User_id") Long userid, @RequestParam("date") LocalDate date,
            Model model) {
        Long logCount = dLogRepo.checkLogsForDate(userid, date);
        if (logCount == 0) {
            String day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }

        model.addAttribute("Program", dLogRepo.findByDate(userid, date, getActiveProgramID()));
        model.addAttribute("date", date);
        return FragmentsRendering
                .fragment("fragments/Details/Date.html :: new_date(date = ${date}, Program = ${Program})").build();
    }

    @HxRequest
    @GetMapping("/Logs/edit")
    public View showDailyLogPopup(@ModelAttribute("User_id") Long userid, @RequestParam("date") LocalDate date,
            Model model) {
        EntryDTO dto = new EntryDTO();
        dto.setLogs(dLogRepo.findByDate(userid, date, getActiveProgramID()));
        dto.setValues();

        model.addAttribute("Program", dto);
        model.addAttribute("date", date);
        model.addAttribute("logstatus", log_status.values());
        return FragmentsRendering
                .fragment("fragments/popups/date-edit.html :: dlp_edit(date = ${date}, Program = ${Program})").build();
    }

    @HxRequest
    @GetMapping("/Logs/add")
    public View showDailyLogEditrow(@ModelAttribute("User_id") Long userid, @RequestParam("index") int index,
            Model model) {
        model.addAttribute("index", index);
        model.addAttribute("logstatus", log_status.values());
        DLEditIncrement();
        model.addAttribute("count", dledit);
        return FragmentsRendering.fragment("fragments/Tables/new-row :: dl_edit_row(${index})").build();
    }

    @HxRequest
    @DeleteMapping("/Logs/delete")
    public String deleteDailyLogEditrow() {
        return "";
    }

    @PostMapping("/Logs/edit")
    public String showDailyLogPostEdit(@ModelAttribute("User_id") Long userid, @ModelAttribute("Program") EntryDTO edit,
            Model model) {
        String duration, comment, day = edit.getDay(), status;
        LocalDate date = edit.getDate();
        Long exerid, currEntries = pLogRepo.countByCurrentProgram(userid, getActiveProgramID()), log_id;
        Integer weights, sets, reps;

        for (int i = 0; i < currEntries; i++) {
            log_id = edit.getLogs().get(0).getLog_id();
            exerid = exerciserepo.findByNameWithUser(edit.getLogs().get(0).getExercise_name(), userid).getExercise_id();
            weights = edit.getLogs().get(0).getWeights();
            sets = edit.getLogs().get(0).getSets();
            reps = edit.getLogs().get(0).getReps();
            duration = edit.getLogs().get(0).getDuration();
            comment = edit.getLogs().get(0).getComment();
            status = edit.getLogs().get(0).getLog_status().toString();

            dLogRepo.updateDailyLog(userid, getActiveProgramID(), log_id, exerid, day, weights, sets, reps, duration,
                    comment,
                    status,
                    date);

            edit.getLogs().remove(0);
        }

        for (int i = 0; i < edit.getLogs().size(); i++) {
            exerid = exerciserepo.findByNameWithUser(edit.getLogs().get(i).getExercise_name(), userid).getExercise_id();
            weights = edit.getLogs().get(i).getWeights();
            sets = edit.getLogs().get(i).getSets();
            reps = edit.getLogs().get(i).getReps();
            duration = edit.getLogs().get(i).getDuration();
            comment = edit.getLogs().get(i).getComment();
            status = edit.getLogs().get(i).getLog_status().toString();

            dLogRepo.insertDailyLog(userid, getActiveProgramID(), exerid, day, weights, sets, reps, duration, comment,
                    status,
                    date);
        }

        return "redirect:/dailyLog";
    }

    @SuppressWarnings("null")
    @GetMapping("/Programs")
    public String showPrograms(@ModelAttribute("User") Optional<User> user, @ModelAttribute("User_id") Long userid,
            Model model) {
        model.addAttribute("currentProgram", getActiveProgram());
        model.addAttribute("programname", getActiveProgramName());
        model.addAttribute("programId", getActiveProgramID());

        Map<String, Long> programsMap = new HashMap<String, Long>();
        List<String> ipstat = programrepo.findByInactiveStatus(program_status.valueOf("inactive").toString(), userid)
                .stream()
                .map(programDTO::getProgram_name).collect(Collectors.toList());
        for (String progname : ipstat) {
            programsMap.put(progname, programrepo.findProgramId(progname, userid).getProgram_id());
        }

        model.addAttribute("tag", program_tag.values());
        model.addAttribute("status", program_status.values());
        model.addAttribute("programMap", programsMap);
        return "Programs";
    }

    @HxRequest
    @GetMapping("/Programs/details")
    public View showProgramDetails(@RequestParam("programName") String progname, @RequestParam("programId") Long progId,
            @ModelAttribute("User_id") Long userid,
            Model model) {

        Map<String, List<programDTO>> currentProgram = pLogRepo.findByProgramNamewithDetails(progname, userid)
                .stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new,
                        Collectors.toList()));

        model.addAttribute("Program", currentProgram);
        model.addAttribute("prog_id", progId);

        model.addAttribute("prog_name", progname);

        return FragmentsRendering
                .fragment("fragments/Details/Program :: OProgam(Program = ${Program}, prog_id = ${prog_id})")
                .build();

        // return FragmentsRendering.fragment("fragments/Details/test.html ::
        // test(progname = ${prog_name})").build();
    }

    @HxRequest
    @GetMapping("/Programs/switch")
    public View GetProgramSwitch(@RequestParam("id") Long progid, Model model) {
        model.addAttribute("switchId", progid);
        return FragmentsRendering.fragment("fragments/popups/confirmation :: switch(switchId=${switchId})").build();
    }

    @HxRequest
    @PostMapping("/Programs/switch")
    public String PostProgramSwitch(@RequestParam("id") Long progid, @ModelAttribute("User_id") Long userid) {
        programrepo.updateProgramStatus(program_status.valueOf("active").toString(), progid, userid);
        programrepo.updateProgramStatus(program_status.valueOf("inactive").toString(), getActiveProgramID(), userid);
        setActiveProgram(userid);
        return "redirect:/Programs";
    }

    @HxRequest
    @GetMapping("/Programs/delete")
    public View GetProgramdelete(@RequestParam("id") Long progid, Model model) {
        model.addAttribute("deleteId", progid);
        return FragmentsRendering.fragment("fragments/popups/confirmation :: delete(deleteId=${deleteId})").build();
    }

    @HxRequest
    @DeleteMapping(value = "/Programs/delete")
    public String deleteProgram(@RequestParam("id") Long progid, @ModelAttribute("User_id") Long userid,
            Model model) {
        programrepo.deleteById(progid);
        return "redirect:/Programs";
    }

    @SuppressWarnings("null")
    @HxRequest
    @GetMapping(value = "/Programs/edit")
    public View showProgramPopup(@RequestParam("name") String progname, @RequestParam("id") Long progId,
            @ModelAttribute("User_id") Long userid, Model model) {
        mapDTO dto = new mapDTO();
        dto.setMapDto(pLogRepo.findByProgramNamewithDetails(progname, userid).stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList())));
        dto.setTag(pLogRepo.findByProgramNamewithDetails(progname, userid).stream()
                .collect(Collectors.toMap(programDTO::getDay, programDTO::getProgram_tag)));

        model.addAttribute("Program", dto);
        model.addAttribute("prog_id", progId);
        model.addAttribute("prog_name", progname);
        model.addAttribute("tag", program_tag.values());
        model.addAttribute("status", program_status.values());
        return FragmentsRendering.fragment(
                "fragments/popups/Prog-edit :: Program_edit(Program = ${Program}, prog_name = ${prog_name}, prog_id = ${prog_id})")
                .build();
    }

    @PutMapping(value = "/Programs/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsEdit(@ModelAttribute("Program") mapDTO map, Model model,
            @ModelAttribute("User_id") Long userid) {
        for (Map.Entry<String, Boolean> item : map.getDaypair().entrySet()) {
            if (item.getValue().equals(true)) {
                String duration, day = item.getKey(), progname = map.getProg_name(),
                        tag = map.getTagmap().get(item.getKey()).toString();
                List<programDTO> dto = map.getMapdto().get(item.getKey());
                Long progid = map.getProg_id();
                Long exerid, log_id, currEntries = pLogRepo.countByCurrentProgram(userid, progid);
                Integer sets, reps;

                programrepo.updateProgramName(progname, progid, userid);

                for (int i = 0; i < currEntries; i++) {
                    exerid = exerciserepo.findByNameWithUser(dto.get(0).getExercise_name(), userid).getExercise_id();
                    sets = dto.get(0).getSets();
                    reps = dto.get(0).getReps();
                    duration = dto.get(0).getDuration();
                    log_id = dto.get(0).getPlog_id();

                    pLogRepo.updateProgramLog(userid, progid, log_id, exerid, day, sets, reps, duration, tag);

                    dto.remove(0);
                }

                for (int i = 0; i < dto.size(); i++) {
                    exerid = exerciserepo.findByNameWithUser(dto.get(i).getExercise_name(), userid).getExercise_id();
                    sets = dto.get(i).getSets();
                    reps = dto.get(i).getReps();
                    duration = dto.get(i).getDuration();

                    pLogRepo.insertProgramLog(userid, progid, exerid, day, sets, reps, duration, tag);
                }

            }
        }

        return "redirect:/Programs";
    }

    @HxRequest
    @GetMapping("/Programs/edit-row")
    public View showProgramsEditrow(@RequestParam("day") String day, @RequestParam("index") int index, Model model) {
        model.addAttribute("day", day);
        model.addAttribute("index", index);
        ProgEditIncrement();
        model.addAttribute("count", progedit);
        return FragmentsRendering.fragment("fragments/Tables/new-row :: prog_edit_row(day = ${day}, index = ${index})")
                .build();
    }

    @HxRequest
    @GetMapping("/Programs/add-row")
    public View showProgramsAddrow(@RequestParam("day") String day, @RequestParam("index") int index, Model model) {
        model.addAttribute("day", day);
        model.addAttribute("index", index);
        ProgAddIncrement();
        model.addAttribute("count", progadd);
        return FragmentsRendering.fragment("fragments/Tables/new-row :: prog_add_row(day = ${day}, index = ${index})")
                .build();
    }

    @HxRequest
    @GetMapping("/Programs/Add")
    public View GetProgramsAdd(Model model) {
        mapDTO dto = new mapDTO();
        dto.setList();

        model.addAttribute("dto", dto);
        model.addAttribute("tag", program_tag.values());
        model.addAttribute("status", program_status.values());
        model.addAttribute("days", List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
        return FragmentsRendering.fragment("fragments/popups/Prog-edit :: Add_Prog(dto = ${dto})").build();
    }

    @HxRequest
    @DeleteMapping("/Programs/delete-row")
    public String deleteProgramrow() {
        return "";
    }

    @PostMapping(value = "/Programs/Add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsAdd(@ModelAttribute("dto") mapDTO program, Model model,
            @ModelAttribute("User_id") Long userid) {
        Long exerid, progid = programrepo.insertProgram(program.getProg_name(), userid);
        String duration, day;
        Integer sets, reps;

        for (Map.Entry<String, List<programDTO>> item : program.getMapdto().entrySet()) {
            day = item.getKey();
            String tag = program.getTagmap().get(day).toString();

            for (programDTO dto : item.getValue()) {
                exerid = exerciserepo.findByNameWithUser(dto.getExercise_name(), userid).getExercise_id();
                sets = dto.getSets();
                reps = dto.getReps();
                duration = dto.getDuration();

                pLogRepo.insertProgramLog(userid, progid, exerid, day, sets, reps, duration, tag);
            }

        }

        return "redirect:/Programs";
    }

    @SuppressWarnings("null")
    @GetMapping("/Exercise")
    public String showExercise(@ModelAttribute("User_id") Long userid, Model model) {
        Map<String, List<ExerciseList>> muscle = exerciserepo.findByAllMuscleGroup(userid).stream()
                .collect(Collectors.groupingBy(ExerciseList::getMuscle_group, LinkedHashMap::new, Collectors.toList()));
        Map<String, List<ExerciseList>> category = exerciserepo.findByAllCategory(userid).stream()
                .collect(Collectors.groupingBy(ExerciseList::getCategory, LinkedHashMap::new, Collectors.toList()));

        Map<String, List<ExerciseList>> result = Stream.concat(muscle.entrySet().stream(), category.entrySet().stream())
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> {
                    existing.addAll(replacement);
                    return existing;
                }, LinkedHashMap::new));

        model.addAttribute("all", result);
        return "Exercise";
    }

    @HxRequest
    @GetMapping("/Exercise/add")
    public String getMethodName(@RequestParam("type") String name, Model model) {
        if (Arrays.asList("cardio", "flexibility", "isometrics").contains(name)) {
            model.addAttribute("ExerciseList", new ExerciseList());
            model.addAttribute("category", name);

            return "fragments/popups/exercise-form :: category-form(category=${category})";

        } else {
            model.addAttribute("ExerciseList", new ExerciseList());
            model.addAttribute("muscle", name);

            return "fragments/popups/exercise-form :: muscle-form(muscle=${muscle})";
        }
    }

    @PostMapping(path = "/Exercise", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showExercisePost(Model model, @ModelAttribute("ExerciseList") ExerciseList exerlist) {
        exerciserepo.save(exerlist);
        return "redirect:/Exercise";
    }

    @GetMapping("/Analyze")
    public String showAnalyze(@ModelAttribute("User_id") Long userid, Model model) {
        model.addAttribute("exercisesum",
                dLogRepo.sumByExerciseAndStatus(userid, log_status.valueOf("completed").name()));
        model.addAttribute("SCsum", dLogRepo.countByStatus(userid, log_status.valueOf("completed").name()));
        model.addAttribute("SPsum", dLogRepo.countByStatus(userid, log_status.valueOf("pending").name()));
        model.addAttribute("SIsum", dLogRepo.countByStatus(userid, log_status.valueOf("cancelled").name()));
        model.addAttribute("volumesum", dLogRepo.sumByVolume(userid));
        model.addAttribute("WSRsum", dLogRepo.TotalWSR(userid));
        return "Analyze";
    }

    @SuppressWarnings("null")
    @HxRequest
    @GetMapping("/Analyze/workouts")
    public View showPreWorkouts(@ModelAttribute("User_id") Long userid,
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
        return FragmentsRendering.fragment("fragments/Charts/workChart :: workChart").build();
    }

    @SuppressWarnings("null")
    @HxRequest
    @PostMapping("/Analyze/workouts")
    public View showPostWorkouts(@ModelAttribute("User_id") Long userid,
            @RequestParam(value = "category") String category,
            Model model) {
        if (category.equals("cardio") || category.equals("isometrics") || category.equals("flexibility")) {
            model.addAttribute("workResult", "No data available for this category.");
        }

        List<AnalyzeDTO> postList = dLogRepo.WorkoutsTabCategoryQuery(8, category, userid);
        model.addAttribute("postworklabels", postList.stream().filter(item -> item.getDates() != null)
                .map(AnalyzeDTO::getDates).collect(Collectors.toList())); // String
        model.addAttribute("postworkvolume", postList.stream().filter(item -> item.getTotal_volume() != null)
                .map(AnalyzeDTO::getTotal_volume).collect(Collectors.toList())); // Long
        model.addAttribute("postworkweights", postList.stream().filter(item -> item.getTotal_weight() != null)
                .map(AnalyzeDTO::getTotal_weight).collect(Collectors.toList()));
        model.addAttribute("postworksets", postList.stream().filter(item -> item.getTotal_sets() != null)
                .map(AnalyzeDTO::getTotal_sets).collect(Collectors.toList()));
        model.addAttribute("postworkreps", postList.stream().filter(item -> item.getTotal_reps() != null)
                .map(AnalyzeDTO::getTotal_reps).collect(Collectors.toList()));
        return FragmentsRendering.fragment("fragments/Charts/workChart :: workChart").build();
    }

    @HxRequest
    @GetMapping("/Analyze/breakdown")
    public View showPreBreakdown(@ModelAttribute("User_id") Long userid,
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
        return FragmentsRendering.fragment("fragments/Charts/breakChart :: breakChart").build();
    }

    @HxRequest
    @PostMapping("/Analyze/exercise")
    public View showPostExercises(@ModelAttribute("User_id") Long userid,
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
        return FragmentsRendering.fragment("fragments/Charts/exerciseChart :: exerciseChart").build();
    }

    @HxRequest
    @GetMapping("sign-out")
    public String getSignOut(@RequestParam("Origin") String origin, Model model) {
        model.addAttribute("Origin", origin);
        return "fragments/popups/signout :: signout(Origin=${Origin})";
    }

    @HxRequest
    @PostMapping("sign-out")
    public String postMethodName(@RequestParam("Origin") String origin, Model model, HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire the cookie immediately
        cookie.setSecure(true);
        response.addCookie(cookie);

        if (origin.equals("main")) {
            return "redirect:/api/home";
        } else {
            return "redirect:/api/sign-out";
        }

    }

    @HxRequest
    @GetMapping("/content")
    public String getMethodName(@RequestParam("Name") String name, @RequestParam("Email") String email, Model model) {
        model.addAttribute("Name", name);
        model.addAttribute("Email", email);
        return "fragments/content :: content(Name=${Name}, Email=${Email})";
    }

    @HxRequest
    @GetMapping("/chart")
    public String getChart(Model model) {
        return "fragments/chart :: chart";
    }

}
