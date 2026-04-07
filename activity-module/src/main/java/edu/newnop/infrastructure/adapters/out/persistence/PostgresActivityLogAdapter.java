package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.application.out.ActivityLogRepositoryPort;
import edu.newnop.domain.model.Activity;
import edu.newnop.infrastructure.adapters.out.mapper.ActivityLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostgresActivityLogAdapter implements ActivityLogRepositoryPort {
    private final JpaActivityRepository jpaActivityRepository;

    @Override
    public void save(Activity activity) {
        jpaActivityRepository.save(ActivityLogMapper.mapDomainToEntity(activity));
    }

    @Override
    public Page<Activity> findAll(PageRequest pageRequest) {
        return jpaActivityRepository.findAll(pageRequest)
                .map(ActivityLogMapper::mapEntityToDomain);
    }

    @Override
    public Page<Activity> findAllWithSearchQuery(String searchQuery, PageRequest pageRequest) {
        return jpaActivityRepository.findAllWithSearchQuery(searchQuery, pageRequest).map(ActivityLogMapper::mapEntityToDomain);
    }

    @Override
    public Page<Activity> findAllByUserId(Long userId, PageRequest pageRequest) {
        return jpaActivityRepository.findAllByActorId(userId, pageRequest).map(ActivityLogMapper::mapEntityToDomain);
    }

    @Override
    public Page<Activity> findByUserIdAndSearchQuery(Long userId, String searchQuery, PageRequest pageRequest) {
        return jpaActivityRepository.findByUserIdAndSearchQuery(userId, searchQuery, pageRequest).map(ActivityLogMapper::mapEntityToDomain);
    }
}
