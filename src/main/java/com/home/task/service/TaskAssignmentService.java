package com.home.task.service;

import com.home.task.dto.PersonDTO;
import com.home.task.model.TaskAssignment;
import com.home.task.repository.TaskAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
//import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TaskAssignmentService {

    private final TaskAssignmentRepository repo;

    public TaskAssignmentService(TaskAssignmentRepository repo) {
        this.repo = repo;
    }

    public TaskAssignment assignTasks(
            List<PersonDTO> people,
            String shift,
            String team
    ) {



        ZoneId IST = ZoneId.of("Asia/Kolkata");
        ZonedDateTime now = ZonedDateTime.now(IST);

        LocalDate today = now.toLocalDate();
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        List<PersonDTO> available = people.stream()
                .filter(PersonDTO::isAvailable)
                .toList();

        Map<String, String> result = new HashMap<>();

        // 🚨 No one available
        if (available.isEmpty()) {
            result.put("STATUS", "No one is available");
            return saveOrUpdate(today, time, shift, team, result);
        }

        // 🔹 SHIFT GROUPS
        List<String> morningGroup = List.of("Varun", "Naveen", "Jaswanth");
        List<String> nightGroup = List.of("Akash", "Ankith", "Abbas");

        List<String> activeGroup =
                shift.equalsIgnoreCase("Morning") ? morningGroup : nightGroup;

        List<PersonDTO> shiftAvailable = available.stream()
                .filter(p -> activeGroup.contains(p.getName()))
                .toList();

        boolean fullShiftPresent =
                shiftAvailable.size() == activeGroup.size();

        // ✅ If full shift group present → ignore others
        List<PersonDTO> workingSet =
                fullShiftPresent ? shiftAvailable : available;

        List<PersonDTO> cooks = workingSet.stream()
                .filter(PersonDTO::isCanCook)
                .toList();

        List<PersonDTO> nonCooks = workingSet.stream()
                .filter(p -> !p.isCanCook())
                .toList();

        // 🍽️ No cooks
        if (cooks.isEmpty()) {
            result.put("FOOD", "Order from restaurant");
            return saveOrUpdate(today, time, shift, team, result);
        }

        // 👥 Exactly 2 people → shared work
        if (workingSet.size() == 2) {

            String both =
                    workingSet.get(0).getName() +
                            " & " +
                            workingSet.get(1).getName();

            result.put("COOKING", both);
            result.put("CUTTING", both);
            result.put("DISHES", both);

            return saveOrUpdate(today, time, shift, team, result);
        }

        // 👨‍🍳 Priority cook (ALWAYS fixed)
        PersonDTO cook = selectCookByPriority(workingSet);
        result.put("COOKING", cook.getName());

        // 🧑‍🤝‍🧑 Helpers (excluding cook)
        List<PersonDTO> helpers = new ArrayList<>(nonCooks);
        helpers.addAll(
                workingSet.stream()
                        .filter(p ->
                                p.isCanCook()
                                        && !p.getName().equals(cook.getName()))
                        .toList()
        );

        // 🔁 HISTORY-BASED ROTATION
        rotateHelpersUsingHistory(helpers, shift);

        result.put("CUTTING", helpers.get(0).getName());
        result.put("DISHES", helpers.get(1).getName());

        return saveOrUpdate(today, time, shift, team, result);
    }

    // 🔥 Cooking priority (UNCHANGED)
    private PersonDTO selectCookByPriority(List<PersonDTO> available) {

        List<String> priority = List.of(
                "Akash",
                "Varun",
                "Ankith",
                "Jaswanth"
        );

        for (String name : priority) {
            for (PersonDTO p : available) {
                if (p.isCanCook() && p.getName().equals(name)) {
                    return p;
                }
            }
        }

        return available.get(0); // safety fallback
    }

    // 🔁 HISTORY-BASED HELPER ROTATION
    private void rotateHelpersUsingHistory(
            List<PersonDTO> helpers,
            String shift
    ) {

        if (helpers.size() < 2) return;

        Optional<TaskAssignment> last =
                repo.findTopByShiftOrderByDateDescTimeDesc(shift);

        if (last.isEmpty()) return;

        Map<String, String> prev = last.get().getAssignments();

        String prevCutting = prev.get("CUTTING");
        String prevDishes = prev.get("DISHES");

        // If same helpers are present → swap
        if (helpers.get(0).getName().equals(prevCutting)
                && helpers.get(1).getName().equals(prevDishes)) {

            Collections.swap(helpers, 0, 1);
        }
    }

    // ✅ SAVE OR UPDATE (ONE RECORD PER DAY + SHIFT)
    private TaskAssignment saveOrUpdate(
            LocalDate date,
            String time,
            String shift,
            String team,
            Map<String, String> assignments
    ) {

        TaskAssignment record =
                repo.findByDateAndShift(date, shift)
                        .orElseGet(TaskAssignment::new);

        record.setDate(date);
        record.setTime(time);
        record.setShift(shift);
        record.setTeam(team);
        record.setAssignments(assignments);

        return repo.save(record);
    }

    public List<TaskAssignment> getHistory() {
        return repo.findAllByOrderByDateDescTimeDesc();
    }
}
