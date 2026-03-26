package edu.newnop.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "activity_log")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityEntity extends BaseJPAEntity {
    private Instant createdAt;
    private String description;
}
