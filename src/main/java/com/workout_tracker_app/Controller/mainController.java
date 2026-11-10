package com.workout_tracker_app.Controller;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.ResponseBody;
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

import javax.swing.text.DateFormatter;

import com.workout_tracker_app.Entity.User;
import com.workout_tracker_app.Repository.DailyLogRepo;
import com.workout_tracker_app.Repository.Exerciserepo;
import com.workout_tracker_app.Repository.UserRepository;
import com.workout_tracker_app.Repository.programLogRepo;
import com.workout_tracker_app.dto.AnalyzeDTO;
import com.workout_tracker_app.dto.DLDTO;
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
        ++progedit;
    }

    public void resetProgEdit() {
        progedit = 0;
    }

    public void setProgAdd(int count) {
        progadd = count;
    }

    public void ProgAddIncrement() {
        ++progadd;
    }

    public void resetProgAdd() {
        progadd = 0;
    }

    public void setDLEdit(int count) {
        dledit = count;
    }

    public void DLEditIncrement() {
        ++dledit;
    }

    public void resetDLEdit() {
        dledit = 0;
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
        model.addAttribute("curday", LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        model.addAttribute("dlp_name", getActiveProgramName());
        return "dailyLog";
    }

    @HxRequest
    @GetMapping("/Logs/date")
    public View showDailyLogDate(@ModelAttribute("User_id") Long userid, @RequestParam("date-select") String date,
            Model model) {
        /*
         * Long logCount = dLogRepo.checkLogsForDate(userid, LocalDate.parse(date));
         * if (logCount == 0) {
         * String day =
         * LocalDate.parse(date).getDayOfWeek().getDisplayName(TextStyle.FULL,
         * Locale.ENGLISH);
         * dLogRepo.copyProgramDataForDate(userid, day, LocalDate.parse(date));
         * }
         */

        List<DLDTO> logs = dLogRepo.findByDate(userid, LocalDate.parse(date), getActiveProgramID());

        if (logs.isEmpty()) {
            String day = LocalDate.parse(date).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            dLogRepo.copyProgramDataForDate(userid, day, LocalDate.parse(date));
        }

        model.addAttribute("Program", dLogRepo.findByDate(userid, LocalDate.parse(date), getActiveProgramID()));
        model.addAttribute("date", LocalDate.parse(date));
        model.addAttribute("day", LocalDate.parse(date).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        return FragmentsRendering
                .fragment("fragments/Details/Date.html :: new_date(date = ${date}, day = ${day}, Program = ${Program})")
                .build();

    }

    @HxRequest
    @GetMapping("/Logs/edit")
    public View showDailyLogPopup(@ModelAttribute("User_id") Long userid, @RequestParam("date") String date,
            Model model) {
        EntryDTO dto = new EntryDTO();
        String day = LocalDate.parse(date).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        dto.setLogs(dLogRepo.findByDate(userid, LocalDate.parse(date), getActiveProgramID()));
        setDLEdit(0);
        dto.setDate(date);
        dto.setDay(day);
        dto.setTag(pLogRepo.findByProgramTag(getActiveProgramName(), userid, day).getProgram_tag());

        model.addAttribute("Program", dto);
        model.addAttribute("date", LocalDate.parse(date));
        model.addAttribute("day", day);
        model.addAttribute("logstatus", log_status.values());
        return FragmentsRendering
                .fragment(
                        "fragments/popups/date-edit.html :: dlp_edit(date = ${date}, day = ${Program.day}, Program = ${Program})")
                .build();
    }

    @HxRequest
    @GetMapping("/Logs/add")
    public View showDailyLogEditrow(@ModelAttribute("User_id") Long userid, Model model) {
        model.addAttribute("logstatus", log_status.values());
        model.addAttribute("index", dledit);
        DLEditIncrement();
        model.addAttribute("count", dledit);

        return FragmentsRendering
                .fragment("fragments/Tables/new-row :: dl-edit-row(index = ${index}, count = ${count})").build();

        // return FragmentsRendering.fragment("fragments/Tables/new-row ::
        // dl-row").build();
    }

    @HxRequest
    @DeleteMapping("/Logs/delete")
    @ResponseBody
    public String deleteDailyLogEditrow() {
        return "";
    }

    @PostMapping(value = "/Logs/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showDailyLogPostEdit(@ModelAttribute("User_id") Long userid, @ModelAttribute("Program") EntryDTO edit,
            Model model) {
        String duration, comment, day = edit.getDay(), status;
        LocalDate date = LocalDate.parse(edit.getDate());
        Long exerid, log_id;
        List<DLDTO> logs = edit.getLogs(), newLogs = edit.getNewLogs();
        Integer weights, sets, reps;

        for (DLDTO item : logs) {
            log_id = item.getLog_id();
            exerid = exerciserepo.findByNameWithUser(item.getExercise_name(), userid).getExercise_id();
            weights = item.getWeights();
            sets = item.getSets();
            reps = item.getReps();
            duration = item.getDuration();
            comment = item.getComment();
            status = item.getLog_status().toString();

            dLogRepo.updateDailyLog(getActiveProgramID(), log_id, exerid, day, weights, sets, reps, duration,
                    comment,
                    status,
                    date);
        }

        for (DLDTO item : newLogs) {
            if (item.getExercise_name() == null || item.getExercise_name().isEmpty()) {
                continue; // Skip this iteration if exercise_name is null or empty
            }

            exerid = exerciserepo.findByNameWithUser(item.getExercise_name(), userid).getExercise_id();
            weights = item.getWeights();
            sets = item.getSets();
            reps = item.getReps();
            duration = item.getDuration();
            comment = item.getComment();
            status = item.getLog_status().toString();

            dLogRepo.insertDailyLog(userid, exerid, day, weights, sets, reps, duration, comment,
                    status,
                    date);
        }

        resetDLEdit();

        return "redirect:/api/main/Logs";
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
                .fragment("fragments/Details/Program :: Program(Program = ${Program}, prog_id = ${prog_id})")
                .build();

        // return FragmentsRendering.fragment("fragments/Details/test.html ::
        // test(progname = ${prog_name})").build();
    }

    @HxRequest
    @GetMapping("/Programs/switch")
    public View GetProgramSwitch(@RequestParam("id") Long progid, @RequestParam("name") String progname, Model model) {
        model.addAttribute("switchId", progid);
        model.addAttribute("nprog", progname);
        model.addAttribute("curId", getActiveProgramID());
        model.addAttribute("cprog", getActiveProgramName());

        return FragmentsRendering.fragment(
                "fragments/popups/confirmation :: switch(switchId=${switchId}, nprog=${nprog}, curId=${curId}, cprog=${cprog})")
                .build();
    }

    @PostMapping(value = "/Programs/switch", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String PostProgramSwitch(@RequestParam("id") Long progid, @ModelAttribute("User_id") Long userid) {
        programrepo.updateProgramStatus(program_status.valueOf("active").toString(), progid, userid);
        programrepo.updateProgramStatus(program_status.valueOf("inactive").toString(), getActiveProgramID(), userid);
        setActiveProgram(userid);
        setActiveProgramID(userid);
        setActiveProgramName(userid);
        return "redirect:/api/main/Programs";
    }

    @HxRequest
    @GetMapping("/Programs/delete")
    public View GetProgramdelete(@RequestParam("id") Long progid, @RequestParam("name") String progname, Model model) {
        model.addAttribute("deleteId", progid);
        model.addAttribute("nprog", progname);

        return FragmentsRendering.fragment(
                "fragments/popups/confirmation :: delete(deleteId=${deleteId}, nprog=${nprog})")
                .build();
    }

    @PostMapping(value = "/Programs/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String deleteProgram(@RequestParam("id") Long progid, @ModelAttribute("User_id") Long userid,
            Model model) {
        programrepo.deleteProgram(progid);
        return "redirect:/api/main/Programs";
    }

    @SuppressWarnings("null")
    @HxRequest
    @GetMapping(value = "/Programs/edit")
    public View showProgramPopup(@RequestParam("name") String progname, @RequestParam("id") Long progId,
            @ModelAttribute("User_id") Long userid, Model model) {
        mapDTO dto = new mapDTO();
        dto.setMapDto(pLogRepo.findByProgramNamewithDetails(progname, userid).stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList())));
        Map<String, String> tagsByDay = new LinkedHashMap<>();
        dto.getMapdto().forEach((day, exercises) -> tagsByDay.put(day, exercises.get(0).getProgram_tag()));
        dto.setPairs();
        dto.setLogs();
        dto.setTag(tagsByDay);
        dto.setProg_id(progId);
        dto.setProg_name(progname);
        setProgEdit(0);

        model.addAttribute("Program", dto);
        model.addAttribute("count", progedit);
        // model.addAttribute("prog_name", progname);
        model.addAttribute("tag", program_tag.values());
        model.addAttribute("status", program_status.values());
        return FragmentsRendering.fragment(
                "fragments/popups/Prog-edit :: Program_edit(Program = ${Program})")
                .build();
    }

    @PostMapping(value = "/Programs/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsEdit(@ModelAttribute("Program") mapDTO map, Model model,
            @ModelAttribute("User_id") Long userid) {
        for (Map.Entry<String, Boolean> dayEntry : map.getDaypair().entrySet()) {
            if (dayEntry.getValue().equals(true)) {
                String duration, day = dayEntry.getKey(), progname = map.getProg_name(),
                        tag = map.getTagmap().get(dayEntry.getKey()).toString();
                List<programDTO> EList = map.getMapdto().get(dayEntry.getKey());
                List<programDTO> NList = map.getNewLogs().get(dayEntry.getKey());
                Long progid = map.getProg_id();
                Long exerid, log_id;
                Integer sets, reps;

                programrepo.updateProgramName(progname, progid, userid);

                for (programDTO item : EList) {
                    exerid = exerciserepo.findByNameWithUser(item.getExercise_name(), userid).getExercise_id();
                    sets = item.getSets();
                    reps = item.getReps();
                    duration = item.getDuration();
                    log_id = item.getPlog_id();

                    pLogRepo.updateProgramLog(userid, progid, log_id, exerid, day, sets, reps, duration, tag);
                }

                for (programDTO item : NList) {
                    if (item.getExercise_name() == null || item.getExercise_name().isEmpty()) {
                        continue; // Skip this iteration if exercise_name is null or empty
                    }

                    exerid = exerciserepo.findByNameWithUser(item.getExercise_name(), userid).getExercise_id();
                    sets = item.getSets();
                    reps = item.getReps();
                    duration = item.getDuration();

                    pLogRepo.insertProgramLog(userid, progid, exerid, day, sets, reps, duration, tag);
                }
            }
        }

        resetProgEdit();
        setActiveProgram(userid);
        setActiveProgramName(userid);

        return "redirect:/api/main/Programs";
    }

    @HxRequest
    @GetMapping("/Programs/edit-row")
    public View showProgramsEditrow(@RequestParam("day") String day, Model model) {
        model.addAttribute("day", day);
        model.addAttribute("status", program_status.values());
        model.addAttribute("index", progedit);
        ProgEditIncrement();
        model.addAttribute("count", progedit);
        return FragmentsRendering
                .fragment("fragments/Tables/new-row :: prog_edit_row(day = ${day}, count = ${count}, index = ${index})")
                .build();
    }

    @HxRequest
    @GetMapping("/Programs/add-row")
    public View showProgramsAddrow(@RequestParam("day") String day, Model model) {
        model.addAttribute("day", day);
        model.addAttribute("status", program_status.values());
        model.addAttribute("index", progadd);
        ProgAddIncrement();
        model.addAttribute("count", progadd);
        return FragmentsRendering
                .fragment("fragments/Tables/new-row :: prog_add_row(day = ${day}, count = ${count}, index = ${index})")
                .build();
    }

    @HxRequest
    @GetMapping("/Programs/Add")
    public View GetProgramsAdd(Model model) {
        mapDTO dto = new mapDTO();
        dto.setProg_id(null);
        dto.setProg_name(null);
        dto.setPairs();
        // dto.setTags();
        for (Map.Entry<String, Boolean> entry : dto.getDaypair().entrySet()) {
            for (int i = 0; i < 5; i++) {
                dto.getMapdto().get(entry.getKey()).add(new programDTO());
                dto.getNewLogs().get(entry.getKey()).add(new programDTO());
            }
        }

        setProgAdd(0);
        model.addAttribute("dto", dto);
        model.addAttribute("tag", program_tag.values());
        model.addAttribute("status", program_status.values());
        model.addAttribute("days", List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
        return FragmentsRendering.fragment("fragments/popups/Prog-edit :: Add_Prog(dto = ${dto})").build();
    }

    @HxRequest
    @DeleteMapping("/Programs/delete-row")
    @ResponseBody
    public String deleteProgramrow() {
        return "";
    }

    @PostMapping(value = "/Programs/Add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsAdd(@ModelAttribute("dto") mapDTO program, Model model,
            @ModelAttribute("User_id") Long userid) {
        programrepo.insertProgramH2(program.getProg_name(), userid);

        Long exerid, progid = programrepo.findProgramId(program.getProg_name(), userid).getProgram_id();
        String duration;
        Integer sets, reps;
        Map<String, List<programDTO>> newLogs = program.getNewLogs();
        Map<String, List<programDTO>> mapdto = program.getMapdto();
        List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        for (String day : days) {
            String tag = program.getTagmap().get(day).toString();

            for (programDTO mdto : mapdto.get(day)) {
                if (mdto.getExercise_name() == null || mdto.getExercise_name().isEmpty()) {
                    continue; // Skip this iteration if exercise_name is null or empty
                }

                exerid = exerciserepo.findByNameWithUser(mdto.getExercise_name(), userid).getExercise_id();
                sets = mdto.getSets();
                reps = mdto.getReps();
                duration = mdto.getDuration();

                pLogRepo.insertProgramLog(userid, progid, exerid, day, sets, reps, duration, tag);
            }

            for (programDTO ndto : newLogs.get(day)) {
                if (ndto.getExercise_name() == null || ndto.getExercise_name().isEmpty()) {
                    continue; // Skip this iteration if exercise_name is null or empty
                }

                exerid = exerciserepo.findByNameWithUser(ndto.getExercise_name(), userid).getExercise_id();
                sets = ndto.getSets();
                reps = ndto.getReps();
                duration = ndto.getDuration();

                pLogRepo.insertProgramLog(userid, progid, exerid, day, sets, reps, duration, tag);
            }

        }
        resetProgAdd();
        return "redirect:/api/main/Programs";
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
        result.remove("none");

        model.addAttribute("all", result);
        return "Exercise";
    }

    @HxRequest
    @GetMapping("/Exercise/add")
    public String getMethodName(@RequestParam("type") String name, Model model) {
        model.addAttribute("user", getUser().get());

        if (Arrays.asList("cardio", "flexibility", "isometrics").contains(name)) {
            model.addAttribute("category", name);
            model.addAttribute("exercise", ExerciseList.builder().exercise_name(null).exercise_type("none")
                    .muscle_group("none").category(name).users(getUser().get()).build());

            return "fragments/popups/exercise-form :: category-form(category = ${category}, user = ${user})"; // category=${category},
        } else {
            model.addAttribute("muscle", name);
            model.addAttribute("exercise", ExerciseList.builder().exercise_name(null).exercise_type("none")
                    .muscle_group(name).category("none").users(getUser().get()).build());

            return "fragments/popups/exercise-form :: muscle-form(muscle = ${muscle}, user = ${user})"; // muscle=${muscle},
        }
    }

    @PostMapping(path = "/Exercise", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showExercisePost(Model model, @RequestParam("exercise_name") String name,
            @RequestParam("exercise_type") String type, @RequestParam("muscle_group") String muscle,
            @RequestParam("category") String category) {
        exerciserepo.insertIntoExerciseList(getUserId(), name, type, muscle, category);
        return "redirect:/api/main/Exercise";
    }

    @SuppressWarnings("null")
    @GetMapping("/Analyze")
    public String showAnalyze(@ModelAttribute("User_id") Long userid, Model model) {
        // Metrics Portion
        model.addAttribute("exercisesum",
                dLogRepo.sumByExerciseAndStatus(userid, log_status.valueOf("completed").name()));
        model.addAttribute("SCsum", dLogRepo.countByStatus(userid, log_status.valueOf("completed").name()));
        model.addAttribute("SPsum", dLogRepo.countByStatus(userid, log_status.valueOf("pending").name()));
        model.addAttribute("SIsum", dLogRepo.countByStatus(userid, log_status.valueOf("cancelled").name()));
        model.addAttribute("volumesum", dLogRepo.sumByVolume(userid));
        model.addAttribute("WSRsum", dLogRepo.TotalWSR(userid));

        // Workout Portion
        List<AnalyzeDTO> preworkList = dLogRepo.WorkoutsTabPlainQuery(8, userid);

        model.addAttribute("preworklabels", preworkList.stream().filter(item -> item.getDates() != null)
                .map(item -> item.getDates().format(DateTimeFormatter.ofPattern("dd MM yyyy")))
                .collect(Collectors.toList()));
        model.addAttribute("preworkdata", preworkList.stream().filter(item -> item.getVolume() != null)
                .map(AnalyzeDTO::getVolume).collect(Collectors.toList()));
        /*
         * model.addAttribute("preworkweights", preworkList.stream().filter(item ->
         * item.getTotal_weight() != null)
         * .map(AnalyzeDTO::getTotal_weight).collect(Collectors.toList()));
         * model.addAttribute("preworksets", preworkList.stream().filter(item ->
         * item.getTotal_sets() != null)
         * .map(AnalyzeDTO::getTotal_sets).collect(Collectors.toList()));
         * model.addAttribute("preworkreps", preworkList.stream().filter(item ->
         * item.getTotal_reps() != null)
         * .map(AnalyzeDTO::getTotal_reps).collect(Collectors.toList()));
         */

        // Breakdown Portion
        List<AnalyzeDTO> prebreakList = dLogRepo.BreakdownTabQuery(8, userid);

        model.addAttribute("breakmuscle", prebreakList.stream().filter(item -> item.getName() != null)
                .map(AnalyzeDTO::getName)
                .collect(Collectors.toList()));
        /*
         * model.addAttribute("breakWeights", totalWeights);
         * model.addAttribute("breakSets", totalSets);
         * model.addAttribute("breakReps", totalReps);
         */
        model.addAttribute("breakdata", prebreakList.stream().filter(item -> item.getVolume() != null)
                .map(AnalyzeDTO::getVolume).collect(Collectors.toList()));

        return "Analyze";
    }

    @SuppressWarnings("null")
    @HxRequest
    @GetMapping("/Analyze/workouts")
    public View showPreWorkouts(@ModelAttribute("User_id") Long userid,
            @RequestParam(value = "workcategory") String workcategory,
            @RequestParam(value = "workfilter") String workfilter,
            Model model) {
        if (workcategory.equals("cardio") || workcategory.equals("isometrics") || workcategory.equals("flexibility")) {
            model.addAttribute("workResult", "No data available for this category.");
            model.addAttribute("workfilter", workfilter);
            model.addAttribute("pwl", List.of());
            model.addAttribute("pwd", List.of());
        } else if (workcategory.equals("none")) {
            List<AnalyzeDTO> noneList = dLogRepo.WorkoutsTabPlainQuery(8, userid);

            switch (workfilter) {
                case "volume":
                    model.addAttribute("pwd",
                            noneList.stream().filter(item -> item.getVolume() != null)
                                    .map(AnalyzeDTO::getVolume).collect(Collectors.toList()));
                    break;

                case "weights":
                    model.addAttribute("pwd",
                            noneList.stream().filter(item -> item.getWeights() != null)
                                    .map(AnalyzeDTO::getWeights).collect(Collectors.toList()));
                    break;

                case "sets":
                    model.addAttribute("pwd", noneList.stream().filter(item -> item.getSets() != null)
                            .map(AnalyzeDTO::getSets).collect(Collectors.toList()));
                    break;

                case "reps":
                    model.addAttribute("pwd", noneList.stream().filter(item -> item.getReps() != null)
                            .map(AnalyzeDTO::getReps).collect(Collectors.toList()));
                    break;
            }

            model.addAttribute("workfilter", workfilter);
            model.addAttribute("pwl", noneList.stream().filter(item -> item.getDates() != null)
                    .map(item -> item.getDates().format(DateTimeFormatter.ofPattern("dd MM yyyy")))
                    .collect(Collectors.toList()));

        } else {
            List<AnalyzeDTO> nameList = dLogRepo.WorkoutsTabMuscleGroupQuery(8, workcategory, userid);

            switch (workfilter) {
                case "volume":
                    model.addAttribute("pwd",
                            nameList.stream().filter(item -> item.getVolume() != null)
                                    .map(AnalyzeDTO::getVolume).collect(Collectors.toList()));
                    break;

                case "weights":
                    model.addAttribute("pwd",
                            nameList.stream().filter(item -> item.getWeights() != null)
                                    .map(AnalyzeDTO::getWeights).collect(Collectors.toList()));
                    break;

                case "sets":
                    model.addAttribute("pwd", nameList.stream().filter(item -> item.getSets() != null)
                            .map(AnalyzeDTO::getSets).collect(Collectors.toList()));
                    break;

                case "reps":
                    model.addAttribute("pwd", nameList.stream().filter(item -> item.getReps() != null)
                            .map(AnalyzeDTO::getReps).collect(Collectors.toList()));
                    break;
            }

            model.addAttribute("workfilter", workfilter);
            model.addAttribute("pwl", nameList.stream().filter(item -> item.getDates() != null)
                    .map(item -> item.getDates().format(DateTimeFormatter.ofPattern("dd MM yyyy")))
                    .collect(Collectors.toList()));
        }

        return FragmentsRendering.fragment("fragments/Charts/workChart :: workChart").build();
    }

    @HxRequest
    @GetMapping("/Analyze/breakdown")
    public View showPreBreakdown(@ModelAttribute("User_id") Long userid,
            @RequestParam(value = "breakfilter") String breakfilter,
            Model model) {
        List<AnalyzeDTO> breakList = dLogRepo.BreakdownTabQuery(8, userid);

        switch (breakfilter) {
            case "volume":

                model.addAttribute("pbd", breakList.stream().filter(item -> item.getVolume() != null)
                        .map(AnalyzeDTO::getVolume).collect(Collectors.toList()));
                break;

            case "weights":
                model.addAttribute("pbd", breakList.stream().filter(item -> item.getWeights() != null)
                        .map(AnalyzeDTO::getWeights).collect(Collectors.toList()));
                break;

            case "sets":
                model.addAttribute("pbd", breakList.stream().filter(item -> item.getSets() != null)
                        .map(AnalyzeDTO::getSets).collect(Collectors.toList()));
                break;

            case "reps":
                model.addAttribute("pbd", breakList.stream().filter(item -> item.getReps() != null)
                        .map(AnalyzeDTO::getReps).collect(Collectors.toList()));
                break;
        }

        model.addAttribute("breakfilter", breakfilter);
        model.addAttribute("pbm", breakList.stream().filter(item -> item.getName() != null)
                .map(AnalyzeDTO::getName).collect(Collectors.toList()));
        return FragmentsRendering.fragment("fragments/Charts/breakChart :: breakChart").build();
    }

    @HxRequest
    @GetMapping("/Analyze/exercise")
    public View showPostExercises(@ModelAttribute("User_id") Long userid,
            @RequestParam(value = "exercise") String exercise,
            @RequestParam(value = "exercisefilter") String exercisefilter,
            Model model) {
        List<AnalyzeDTO> preList = dLogRepo.ExerciseTabQuery(8, exercise, userid);

        switch (exercisefilter) {
            case "volume":
                model.addAttribute("ped", preList.stream().filter(item -> item.getVolume() != null)
                        .map(AnalyzeDTO::getVolume).collect(Collectors.toList()));
                break;

            case "weights":
                model.addAttribute("ped", preList.stream().filter(item -> item.getWeights() != null)
                        .map(AnalyzeDTO::getWeights).collect(Collectors.toList()));
                break;

            case "sets":
                model.addAttribute("ped", preList.stream().filter(item -> item.getSets() != null)
                        .map(AnalyzeDTO::getSets).collect(Collectors.toList()));
                break;

            case "reps":
                model.addAttribute("ped", preList.stream().filter(item -> item.getReps() != null)
                        .map(AnalyzeDTO::getReps).collect(Collectors.toList()));
                break;

        }

        model.addAttribute("exercisefilter", exercisefilter);
        model.addAttribute("pel", preList.stream().filter(item -> item.getDates() != null)
                .map(item -> item.getDates().format(DateTimeFormatter.ofPattern("dd MM yyyy")))
                .collect(Collectors.toList()));

        return FragmentsRendering.fragment("fragments/Charts/exerciseChart :: exerciseChart").build();
    }

    @HxRequest
    @GetMapping("sign-out")
    public String getSignOut(@RequestParam("final") String finalPage,
            Model model) {
        model.addAttribute("final", finalPage);
        return "fragments/popups/signout :: signout(final=${final})";
    }

    @HxRequest
    @PostMapping("sign-out")
    public String postMethodName(@RequestParam("final") String finalPage,
            Model model, HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire the cookie immediately
        cookie.setSecure(true);
        response.addCookie(cookie);

        if (finalPage.equals("main")) {
            return "redirect:/api/home";
        } else {
            return "redirect:/api/sign-out";
        }

    }

}
