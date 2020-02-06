package org.brasbat.entityviewdemoapp.demo.repository;

import org.brasbat.entityviewdemoapp.demo.entity.TestEntityOne;
import org.springframework.data.repository.CrudRepository;

public interface TestEntityOneRepository extends CrudRepository<TestEntityOne, Long>
{
}
