package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.application.out.TaskRepositoryPort;
import edu.newnop.domain.model.Task;
import edu.newnop.infrastructure.adapters.out.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    @Override
    public Page<Task> findAllByUserId(Long assignedUserId, PageRequest pageRequest) {
        Page<TaskEntity> entities = jpaTaskRepository.findAllByAssignedUserId(assignedUserId, pageRequest);
        return entities.map(TaskMapper::toDomain);
    }

    @Override
    public Optional<Task> findByIdAndUserId(Long taskId, Long UserId) {
        Optional<TaskEntity> entityOpt = jpaTaskRepository.findByIdAndAssignedUserId(taskId, UserId);
        return entityOpt.map(TaskMapper::toDomain);
    }

    @Override
    public void delete(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        jpaTaskRepository.delete(entity);
    }

    @Override
    public Page<Task> findByUserIdAndSearchQuery(Long aLong, String searchQuery, PageRequest pageRequest) {
        return jpaTaskRepository.findByAssignedUserIdAndSearchQuery(aLong, searchQuery, pageRequest)
                .map(TaskMapper::toDomain);
    }

    @Override
    public Page<Task> findAll(PageRequest pageRequest) {
        return jpaTaskRepository.findAll(pageRequest)
                .map(TaskMapper::toDomain);
    }

    @Override
    public Page<Task> findAllWithSearchQuery(String searchQuery, PageRequest pageRequest) {
        return jpaTaskRepository.findAllWithSearchQuery(searchQuery, pageRequest)
                .map(TaskMapper::toDomain);
    }
}
