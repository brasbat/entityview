package org.brasbat.entityview.entityviewreact.repository;

import org.brasbat.entityview.entityviewreact.entity.TestEntityOne;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<TestEntityOne, Long>
{
}
