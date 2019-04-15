package org.brasbat.entityview.entityviewreact;

import org.brasbat.entityview.entityviewreact.entity.TestEntityOne;
import org.brasbat.entityview.entityviewreact.entity.TestEntityTwo;
import org.brasbat.entityview.entityviewreact.entity.TestColumnTypes;
import org.brasbat.entityview.entityviewreact.entity.TestEnum;
import org.brasbat.entityview.entityviewreact.repository.EmployeeRepository;
import org.brasbat.entityview.entityviewreact.repository.EmployeeTwoRepository;
import org.brasbat.entityview.entityviewreact.repository.TestColumnTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class DemoData implements CommandLineRunner
{
    @Autowired
    private EmployeeRepository repo1;

    @Autowired
    private EmployeeTwoRepository repo2;

    @Autowired
    private TestColumnTypesRepository testColumnTypesRepository;

    @Override
    public void run(String... args) throws Exception
    {
        repo1.save(new TestEntityOne("E1", 10, 1));
        repo1.save(new TestEntityOne("E2", 11, 2));
        repo1.save(new TestEntityOne("E3", 12, 3));

        for (int i = 0; i < 1000; i++)
        {
            repo2.save(new TestEntityTwo("E" + (i + 4), i, i + 1));

        }
        for (int i = 0; i < 10; i++)
        {
            TestColumnTypes testColumnTypes = new TestColumnTypes();
            testColumnTypes.setBooleanCol(i % 2 == 0);
            testColumnTypes.setIntCol(i);
            testColumnTypes.setLongCol((long) i + Integer.MAX_VALUE);
            testColumnTypes.setFloatCol((float) i + (float) Math.random());
            testColumnTypes.setStringcol("String" + i);
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.MINUTE, i + 1000);
            testColumnTypes.setTestDate(instance.getTime());
            testColumnTypes.setTestEnum(TestEnum.values()[i % TestEnum.values().length]);
            testColumnTypesRepository.save(testColumnTypes);
        }

    }
}