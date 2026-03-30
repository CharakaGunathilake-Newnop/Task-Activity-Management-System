package edu.newnop.application.out;

import edu.newnop.domain.model.Task;

public interface TaskRepositoryPort {
    Task save(Task task);
}
