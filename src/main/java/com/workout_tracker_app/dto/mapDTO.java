package com.workout_tracker_app.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class mapDTO {
    private Map<String, List<programDTO>> mapdto = new HashMap<String, List<programDTO>>();
    private String prog_name;
    private Long prog_id;
    private Map<String, String> tagmap;
    private Map<String, Boolean> daypair = new HashMap<String, Boolean>();
    private Map<String, List<programDTO>> newLogs = new HashMap<String, List<programDTO>>();

    public mapDTO() {
        setList();
        setLogs();
    }

    public void setMapDto(Map<String, List<programDTO>> mapdto) {
        this.mapdto = mapdto;
    }

    public void setList() {
        mapdto.put("Monday", new ArrayList<programDTO>(5));
        mapdto.put("Tuesday", new ArrayList<programDTO>(5));
        mapdto.put("Wednesday", new ArrayList<programDTO>(5));
        mapdto.put("Thursday", new ArrayList<programDTO>(5));
        mapdto.put("Friday", new ArrayList<programDTO>(5));
        mapdto.put("Saturday", new ArrayList<programDTO>(5));
        mapdto.put("Sunday", new ArrayList<programDTO>(5));
    }

    public void setPairs() {
        daypair.put("Monday", false);
        daypair.put("Tuesday", false);
        daypair.put("Wednesday", false);
        daypair.put("Thursday", false);
        daypair.put("Friday", false);
        daypair.put("Saturday", false);
        daypair.put("Sunday", false);
    }

    public void setTag(Map<String, String> tagmap) {
        this.tagmap = tagmap;
    }

    public void setNewLogs(Map<String, List<programDTO>> newLogs) {
        this.newLogs = newLogs;
    }

    public void setLogs() {
        newLogs.put("Monday", new ArrayList<programDTO>(5));
        newLogs.put("Tuesday", new ArrayList<programDTO>(5));
        newLogs.put("Wednesday", new ArrayList<programDTO>(5));
        newLogs.put("Thursday", new ArrayList<programDTO>(5));
        newLogs.put("Friday", new ArrayList<programDTO>(5));
        newLogs.put("Saturday", new ArrayList<programDTO>(5));
        newLogs.put("Sunday", new ArrayList<programDTO>(5));
    }

}
