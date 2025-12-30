package com.home.task.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Document(collection = "task_assignments")
public class TaskAssignment {

    @Id
    private String id;

    private LocalDate date;
    private String time;   // HH:mm
    private String shift;
    private String team;
    private Map<String, String> assignments;

    public TaskAssignment() {}

    public TaskAssignment(
            LocalDate date,
            String time,
            String shift,
            String team,
            Map<String, String> assignments
    ) {
        this.date = date;
        this.time = time;
        this.shift = shift;
        this.team = team;
        this.assignments = assignments;
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getShift() { return shift; }
    public String getTeam() { return team; }
    public Map<String, String> getAssignments() { return assignments; }

    public void setId(String id) { this.id = id; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setShift(String shift) { this.shift = shift; }
    public void setTeam(String team) { this.team = team; }
    public void setAssignments(Map<String, String> assignments) {
        this.assignments = assignments;
    }
}
