package org.brasbat.entityview.entityviewreact.repository;

import org.brasbat.entityview.entityviewreact.entity.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Long>
{
}
