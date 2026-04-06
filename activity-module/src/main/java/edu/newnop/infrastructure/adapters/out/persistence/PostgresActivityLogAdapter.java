package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.application.out.ActivityLogRepositoryPort;
import edu.newnop.domain.model.Activity;
import edu.newnop.infrastructure.adapters.out.mapper.ActivityLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostgresActivityLogAdapter implements ActivityLogRepositoryPort {
    private final JpaActivityRepository jpaActivityRepository;

    @Override
    public void save(Activity activity) {
        jpaActivityRepository.save(ActivityLogMapper.mapDomainToEntity(activity));
    }
}
