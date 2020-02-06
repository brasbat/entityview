package org.brasbat.entityviewdemoapp.demo;

import java.util.Calendar;

import org.brasbat.entityviewdemoapp.demo.entity.TestColumnTypes;
import org.brasbat.entityviewdemoapp.demo.entity.TestEntityOne;
import org.brasbat.entityviewdemoapp.demo.entity.TestEntityTwo;
import org.brasbat.entityviewdemoapp.demo.entity.TestEnum;
import org.brasbat.entityviewdemoapp.demo.entity.TestObjectDto;
import org.brasbat.entityviewdemoapp.demo.repository.TestEntityOneRepository;
import org.brasbat.entityviewdemoapp.demo.repository.TestEntityTwoRepository;
import org.brasbat.entityviewdemoapp.demo.repository.TestColumnTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoData implements CommandLineRunner
{
    @Autowired
    private TestEntityOneRepository repo1;

    @Autowired
    private TestEntityTwoRepository repo2;

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
	        TestObjectDto dto = new TestObjectDto();
	        dto.setActive(true);
	        dto.setKey(5000+i);
	        dto.setName("5000" + "bla");
            testColumnTypes.setTestNestedObject(dto);
            testColumnTypesRepository.save(testColumnTypes);
        }

    }
}