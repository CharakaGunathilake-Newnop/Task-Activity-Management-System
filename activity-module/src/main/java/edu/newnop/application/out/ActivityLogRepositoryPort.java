package edu.newnop.application.out;

import edu.newnop.domain.model.Activity;

public interface ActivityLogRepositoryPort {
    void save (Activity activity);
}
