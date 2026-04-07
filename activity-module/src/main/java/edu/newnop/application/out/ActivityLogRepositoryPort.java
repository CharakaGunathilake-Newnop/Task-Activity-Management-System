package edu.newnop.application.out;

import edu.newnop.domain.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ActivityLogRepositoryPort {
    void save (Activity activity);

    Page<Activity> findAll(PageRequest pageRequest);

    Page<Activity> findAllWithSearchQuery(String searchQuery, PageRequest pageRequest);

    Page<Activity> findAllByUserId(Long userId, PageRequest pageRequest);

    Page<Activity> findByUserIdAndSearchQuery(Long userId, String searchQuery, PageRequest pageRequest);
}
