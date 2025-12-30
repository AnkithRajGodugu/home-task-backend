package com.home.task.repository;

import com.home.task.model.TaskAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository
        extends MongoRepository<TaskAssignment, String> {
    Optional<TaskAssignment> findByDateAndShift(LocalDate date, String shift);
    List<TaskAssignment> findAllByOrderByDateDescTimeDesc();
}
