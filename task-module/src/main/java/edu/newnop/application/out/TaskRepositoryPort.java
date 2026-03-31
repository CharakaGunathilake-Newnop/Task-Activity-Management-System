package edu.newnop.application.out;

import edu.newnop.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);

    Page<Task> findAllByUserId(Long aLong, PageRequest pageRequest);

    Optional<Task> findByIdAndUserId(Long aLong, Long aLong1);

    void delete(Task task);

    Page<Task> findByUserIdAndSearchQuery(Long aLong, String searchQuery, PageRequest pageRequest);

    Page<Task> findAll(PageRequest pageRequest);

    Page<Task> findAllWithSearchQuery(String searchQuery, PageRequest pageRequest);
}