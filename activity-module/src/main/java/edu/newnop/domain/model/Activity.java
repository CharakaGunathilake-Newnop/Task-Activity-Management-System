package edu.newnop.domain.model;

import edu.newnop.common.model.ActionType;
import edu.newnop.common.model.BaseDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity extends BaseDomainEntity {
    private String entityName;
    private Long entityId;
    private ActionType actionType;
    private String description;
    private Long actorId;
}
