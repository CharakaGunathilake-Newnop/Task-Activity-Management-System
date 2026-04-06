package edu.newnop.infrastructure.adapters.out.mapper;

import edu.newnop.domain.model.Activity;
import edu.newnop.infrastructure.adapters.out.persistence.ActivityEntity;

public class ActivityLogMapper {

    private ActivityLogMapper() {
    }

    public static ActivityEntity mapDomainToEntity(Activity activity) {
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setId(activity.getId() != null ? activity.getId() : null);
        activityEntity.setVersion(activity.getVersion());
        activityEntity.setCreatedAt(activity.getCreatedAt() != null ? activity.getCreatedAt() : null);
        activityEntity.setLastUpdate(activity.getLastUpdate() != null ? activity.getLastUpdate() : null);
        activityEntity.setEntityName(activity.getEntityName());
        activityEntity.setEntityId(activity.getEntityId());
        activityEntity.setActionType(activity.getActionType());
        activityEntity.setDescription(activity.getDescription());
        activityEntity.setActorId(activity.getActorId());
        return activityEntity;
    }

    public static Activity mapEntityToDomain(ActivityEntity activityEntity) {
        Activity activity = new Activity();
        activity.setId(activityEntity.getId());
        activity.setVersion(activityEntity.getVersion());
        activity.setCreatedAt(activityEntity.getCreatedAt());
        activity.setLastUpdate(activityEntity.getLastUpdate());
        activity.setEntityName(activityEntity.getEntityName());
        activity.setEntityId(activityEntity.getEntityId());
        activity.setActionType(activityEntity.getActionType());
        activity.setDescription(activityEntity.getDescription());
        activity.setActorId(activityEntity.getActorId());
        return activity;
    }
}
