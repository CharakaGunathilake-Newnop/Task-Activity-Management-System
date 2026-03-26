package edu.newnop.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDomainEntity {
    private Long id;
    private long version;
    private Instant createdAt;
    private Instant lastUpdate;
}

