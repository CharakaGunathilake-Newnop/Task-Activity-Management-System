package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.common.model.ActionType;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "activity_log")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityEntity extends BaseActivityJpaEntity {
    @Column(nullable = false)
    private String entityName;
    @Column(nullable = false)
    private Long entityId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Long actorId;
}
