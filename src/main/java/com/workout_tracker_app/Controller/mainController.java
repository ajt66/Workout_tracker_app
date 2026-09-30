package com.workout_tracker_app.Controller;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.FragmentsRendering;

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
import com.workout_tracker_app.dto.AnalyzeDTO;
import com.workout_tracker_app.dto.EntryDTO;
import com.workout_tracker_app.dto.mapDTO;
import com.workout_tracker_app.dto.programDTO;
import com.workout_tracker_app.Entity.DailyLog.logStatus;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

import com.workout_tracker_app.Repository.programRepo;

import lombok.RequiredArgsConstructor;

@Controller
@SessionAttributes({ "User", "User-id", "curProgram" })
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class mainController {
    private final UserRepository userRepo;
    private final Exerciserepo exerciserepo;
    private final programRepo programrepo;
    private final programLogRepo pLogRepo;
    private final DailyLogRepo dLogRepo;
    private Map<String, List<programDTO>> currentProgram = new HashMap<String, List<programDTO>>();
    private String programName = "";
    private Long programid;
    private int progedit, progadd, dledit;

    @GetMapping("")
    public String showMain(Model model) {
        // Get the authenticated user's username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepo.findUserId(username);
        setActiveProgramDetails(userId);

        dLogRepo.copyProgramDataForToday(userId);
        dLogRepo.changeStatus(userId);
        model.addAttribute("User", userRepo.findByUsername(username).get());
        model.addAttribute("User-id", userId);
        return "main";
    }

    @GetMapping("/Logs")
    public String showDailyLog(@ModelAttribute("User-id") Long userid, Model model) {
        model.addAttribute("logstatus", logStatus.values());
        model.addAttribute("curProgram", dLogRepo.findByCurrentDate(userid, programid));
        model.addAttribute("curdate", LocalDate.now());
        model.addAttribute("dlp-name", programName);
        return "dailyLog";
    }

    @HxRequest
    @GetMapping("/Logs/date")
    public View showDailyLogDate(@ModelAttribute("User-id") Long userid, @RequestParam("date") LocalDate date,
            Model model) {
        model.addAttribute("Program", dLogRepo.findByDate(userid, date, programid));
        model.addAttribute("date", date);
        return FragmentsRendering
                .fragment("fragments/Switch/Date.html :: new-date(date = ${date}, Program = ${Program})").build();
    }

    @HxRequest
    @GetMapping("/Logs/edit")
    public View showDailyLogPopup(@ModelAttribute("User-id") Long userid, @RequestParam("date") LocalDate date,
            Model model) {
        EntryDTO dto = new EntryDTO();
        dto.setLogs(dLogRepo.findByDate(userid, date, programid));
        dto.setValues();

        model.addAttribute("Program", dto);
        model.addAttribute("date", date);
        model.addAttribute("logstatus", logStatus.values());
        return FragmentsRendering
                .fragment("fragments/popups/date-edit.html :: dlp-edit(date = ${date}, Program = ${Program})").build();
    }

    @HxRequest
    @GetMapping("/Logs/add")
    public View showDailyLogEditrow(@ModelAttribute("User-id") Long userid, @RequestParam("index") int index,
            Model model) {
        model.addAttribute("index", index);
        model.addAttribute("logstatus", logStatus.values());
        DLEditIncrement();
        model.addAttribute("count", dledit);
        return FragmentsRendering.fragment("fragments/Tables/new-row :: dl-edit-row(${index})").build();
    }

    @HxRequest
    @GetMapping("/Logs/delete")
    public String deleteDailyLogEditrow() {
        return "";
    }

    @PostMapping("/Logs/edit")
    public String showDailyLogPostEdit(@ModelAttribute("User-id") Long userid, @ModelAttribute("Program") EntryDTO edit,
            Model model) {
        String duration, comment, day = edit.getDay();
        LocalDate date = edit.getDate();
        Long exerid, currEntries = pLogRepo.countByCurrentProgram(userid, programid), log_id;
        Integer weights, sets, reps;
        logStatus status;

        for (int i = 0; i < currEntries; i++) {
            log_id = edit.getLogs().get(0).getLog_id();
            exerid = exerciserepo.findByNameWithUser(edit.getLogs().get(0).getExercise_name(), userid).getExercise_id();
            weights = edit.getLogs().get(0).getWeights();
            sets = edit.getLogs().get(0).getSets();
            reps = edit.getLogs().get(0).getReps();
            duration = edit.getLogs().get(0).getDuration();
            comment = edit.getLogs().get(0).getComment();
            status = edit.getLogs().get(0).getLog_status();

            dLogRepo.updateDailyLog(userid, programid, log_id, exerid, day, weights, sets, reps, duration, comment,
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
            status = edit.getLogs().get(i).getLog_status();

            dLogRepo.insertDailyLog(userid, programid, exerid, day, weights, sets, reps, duration, comment, status,
                    date);
        }

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

        model.addAttribute("tag", Tag.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("otherProgram", finalData);
        return "Programs";
    }

    @HxRequest
    @GetMapping("/Programs/switch")
    public View GetProgramSwitch(@RequestParam("id") Long progid, Model model) {
        model.addAttribute("switchId", progid);
        return FragmentsRendering.fragment("fragments/popups/confirmation :: switch").build();
    }

    @HxRequest
    @PostMapping("/Programs/switch")
    public String PostProgramSwitch(@RequestParam("id") Long progid, @ModelAttribute("User-id") Long userid) {
        programrepo.updateProgramStatus(Status.valueOf("active"), progid, userid);
        programrepo.updateProgramStatus(Status.valueOf("inactive"), programid, userid);
        setActiveProgramDetails(userid);
        return "redirect:/Programs";
    }

    @HxRequest
    @GetMapping("/Programs/delete")
    public View GetProgramdelete(@RequestParam("id") Long progid, Model model) {
        model.addAttribute("deleteId", progid);
        return FragmentsRendering.fragment("fragments/popups/confirmation :: delete").build();
    }

    @HxRequest
    @PostMapping(value = "/Programs/delete")
    public String deleteProgram(@RequestParam("id") Long progid, @ModelAttribute("User-id") Long userid,
            Model model) {
        programrepo.deleteById(progid);
        return "redirect:/Programs";
    }

    @SuppressWarnings("null")
    @HxRequest
    @GetMapping(value = "/Programs/edit")
    public View showProgramPopup(@RequestParam("name") String progname, @RequestParam("id") Long progId,
            @ModelAttribute("User-id") Long userid, Model model) {
        mapDTO dto = new mapDTO();
        dto.setMapDto(pLogRepo.findByProgramNamewithDetails(progname, userid).stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList())));

        model.addAttribute("Program", dto);
        model.addAttribute("prog-id", progId);
        model.addAttribute("prog-name", progname);
        model.addAttribute("tag", Tag.values());
        model.addAttribute("status", Status.values());
        return FragmentsRendering.fragment(
                "fragments/popups/Prog-edit :: Program-edit(Program = ${Program}, prog-name = ${prog-name}, prog-id = ${prog-id})")
                .build();
    }

    @PostMapping(value = "/Programs/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsEdit(@ModelAttribute("Program") mapDTO map, Model model,
            @ModelAttribute("User-id") Long userid) {
        for (Map.Entry<String, Boolean> item : map.getDaypair().entrySet()) {
            if (item.getValue().equals(true)) {
                String duration, day = item.getKey(), progname = map.getProg_name();
                List<programDTO> dto = map.getMapdto().get(item.getKey());
                Long progid = map.getProg_id();
                Long exerid, log_id, currEntries = pLogRepo.countByCurrentProgram(userid, progid);
                Integer sets, reps;
                Tag tag = map.getTag();

                programrepo.updateProgramName(progname, progid, userid);

                for (int i = 0; i < currEntries; i++) {
                    exerid = exerciserepo.findByNameWithUser(dto.get(0).getExercise_name(), userid).getExercise_id();
                    sets = dto.get(0).getSets();
                    reps = dto.get(0).getReps();
                    duration = dto.get(0).getDuration();
                    log_id = dto.get(0).getPlog_id();

                    pLogRepo.updateProgramLog(userid, progid, log_id, exerid, day, sets, reps, duration);

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
        return FragmentsRendering.fragment("fragments/Tables/new-row :: prog-edit-row(day = ${day}, index = ${index})")
                .build();
    }

    @HxRequest
    @GetMapping("/Programs/add-row")
    public View showProgramsAddrow(@RequestParam("day") String day, @RequestParam("index") int index, Model model) {
        model.addAttribute("day", day);
        model.addAttribute("index", index);
        ProgAddIncrement();
        model.addAttribute("count", progadd);
        return FragmentsRendering.fragment("fragments/Tables/new-row :: prog-add-row(day = ${day}, index = ${index})")
                .build();
    }

    @HxRequest
    @GetMapping("/Programs/Add")
    public View GetProgramsAdd(Model model) {
        mapDTO dto = new mapDTO();
        dto.setList();

        model.addAttribute("dto", dto);
        model.addAttribute("tag", Tag.values());
        model.addAttribute("status", Status.values());
        model.addAttribute("days", List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
        return FragmentsRendering.fragment("fragments/popups/Prog-edit :: Add-Prog(dto = ${dto})").build();
    }

    @HxRequest
    @GetMapping("/Programs/delete-row")
    public String deleteProgramrow() {
        return "";
    }

    @PostMapping(value = "/Programs/Add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String showProgramsAdd(@ModelAttribute("dto") mapDTO program, Model model,
            @ModelAttribute("User-id") Long userid) {
        Long exerid, progid = programrepo.insertProgram(program.getProg_name(), userid);
        String duration, day;
        Integer sets, reps;
        Tag tag = program.getTag();

        for (Map.Entry<String, List<programDTO>> item : program.getMapdto().entrySet()) {
            day = item.getKey();
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
    public View showPreWorkouts(@ModelAttribute("User-id") Long userid,
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
        return FragmentsRendering.fragment("fragments/Charts/workChart :: workChartjs").build();
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
        return "";
    }

    @HxRequest
    @GetMapping("/Analyze/breakdown")
    public View showPreBreakdown(@ModelAttribute("User-id") Long userid,
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
        return FragmentsRendering.fragment("fragments/Charts/breakChart :: breakChartjs").build();
    }

    @SuppressWarnings("null")
    @HxRequest
    @GetMapping("/Analyze/exercise")
    public View showPreExercises(Model model, @ModelAttribute("User-id") Long userid) {
        List<AnalyzeDTO> postList = dLogRepo.ExerciseTabDates(8, userid);
        model.addAttribute("exerciseDates", postList.stream().filter(item -> item.getDates() != null)
                .map(AnalyzeDTO::getDates).collect(Collectors.toList()));
        return FragmentsRendering.fragment("fragments/Charts/exerciseChart :: exerciseChartjs").build();
    }

    @HxRequest
    @PostMapping("/Analyze/exercise")
    public View showPostExercises(@ModelAttribute("User-id") Long userid,
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
        return FragmentsRendering.fragment("fragments/Charts/exerciseChart :: exerciseChartjs").build();
    }

    @SuppressWarnings("null")
    public void setActiveProgramDetails(Long userid) {
        programDTO pstat = programrepo.findByActiveStatus(Status.valueOf("active"), userid);
        programName = pstat.getProgram_name();
        programid = programrepo.findProgramId(programName, userid).getProgram_id();
        List<programDTO> pres = pLogRepo.findByProgramNamewithDetails(programName, userid);
        currentProgram = pres.stream()
                .collect(Collectors.groupingBy(programDTO::getDay, LinkedHashMap::new, Collectors.toList()));
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
}
