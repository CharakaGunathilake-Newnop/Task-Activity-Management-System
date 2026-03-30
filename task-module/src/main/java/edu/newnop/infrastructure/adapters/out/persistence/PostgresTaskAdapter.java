package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.application.out.TaskRepositoryPort;
import edu.newnop.domain.model.Task;
import edu.newnop.infrastructure.adapters.out.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostgresTaskAdapter implements TaskRepositoryPort {
    private final JpaTaskRepository jpaTaskRepository;

    @Override
    public Task save(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        TaskEntity savedEntity = jpaTaskRepository.save(entity);
        return TaskMapper.toDomain(savedEntity);
    }
}
