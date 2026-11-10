package com.workout_tracker_app.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class mapDTO {
    private static final List<String> WEEKDAYS = List.of(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

    private Map<String, List<programDTO>> mapdto = new LinkedHashMap<String, List<programDTO>>();
    private String prog_name;
    private Long prog_id;
    private Map<String, String> tagmap;
    private Map<String, Boolean> daypair = new LinkedHashMap<String, Boolean>();
    private Map<String, List<programDTO>> newLogs = new LinkedHashMap<String, List<programDTO>>();

    public mapDTO() {
        setList();
        setLogs();
    }

    public void setMapdto(Map<String, List<programDTO>> mapdto) {
        this.mapdto = orderByWeekday(mapdto);
    }

    public void setMapDto(Map<String, List<programDTO>> mapdto) {
        setMapdto(mapdto);
    }

    public void setList() {
        mapdto = new LinkedHashMap<String, List<programDTO>>();
        WEEKDAYS.forEach(day -> mapdto.put(day, new ArrayList<programDTO>(5)));
    }

    public void setPairs() {
        daypair = new LinkedHashMap<String, Boolean>();
        WEEKDAYS.forEach(day -> daypair.put(day, false));
    }

    public void setTag(Map<String, String> tagmap) {
        this.tagmap = tagmap;
    }

    public void setTags() {
        tagmap = new LinkedHashMap<String, String>();
        WEEKDAYS.forEach(day -> tagmap.put(day, ""));
    }

    public void setNewLogs(Map<String, List<programDTO>> newLogs) {
        this.newLogs = orderByWeekday(newLogs);
    }

    public void setLogs() {
        newLogs = new LinkedHashMap<String, List<programDTO>>();
        WEEKDAYS.forEach(day -> newLogs.put(day, new ArrayList<programDTO>(5)));
    }

    private Map<String, List<programDTO>> orderByWeekday(Map<String, List<programDTO>> logs) {
        Map<String, List<programDTO>> ordered = new LinkedHashMap<String, List<programDTO>>();
        if (logs == null) {
            return ordered;
        }

        WEEKDAYS.forEach(day -> {
            if (logs.containsKey(day)) {
                ordered.put(day, logs.get(day));
            }
        });
        logs.forEach(ordered::putIfAbsent);
        return ordered;
    }

}
