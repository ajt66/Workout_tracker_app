package com.workout_tracker_app.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.workout_tracker_app.Entity.programLog.Tag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class mapDTO {
    private Map<String, List<programDTO>> mapdto;
    private String prog_name;
    private Long prog_id;
    private Tag tag;
    private Map<String, Boolean> daypair;

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
}
