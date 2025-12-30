package com.home.task.controller;

import com.home.task.dto.PersonDTO;
import com.home.task.model.TaskAssignment;
import com.home.task.service.TaskAssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskAssignmentService service;

    public TaskController(TaskAssignmentService service) {
        this.service = service;
    }

    // ✅ HISTORY (VISIBLE TO ALL LOGGED-IN USERS)
    @GetMapping("/history")
    public List<TaskAssignment> history() {
        return service.getHistory();
    }

    // ✅ ASSIGN TASKS
    @PostMapping(
            value = "/assign",
            consumes = "application/json",
            produces = "application/json"
    )
    public TaskAssignment assign(
            @RequestBody Map<String, Boolean> availability,
            @RequestParam String shift,
            @RequestParam String team
    ) {

        List<PersonDTO> people = List.of(
                person("Akash", true),
                person("Varun", true),
                person("Ankith", true),
                person("Jaswanth", true),
                person("Abbas", false),
                person("Naveen", false)
        );

        // apply availability from frontend
        for (PersonDTO p : people) {
            Boolean isAvailable = availability.get(p.getName());
            p.setAvailable(isAvailable != null && isAvailable);
        }

        return service.assignTasks(people, shift, team);
    }

    // 🔒 Single source of truth for people
    private static PersonDTO person(String name, boolean canCook) {
        PersonDTO p = new PersonDTO();
        p.setName(name);
        p.setCanCook(canCook);
        p.setAvailable(false);
        return p;
    }
}
