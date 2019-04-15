package org.brasbat.entityview.entityviewreact.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TestEntityTwo
{
    private @Id
    @GeneratedValue
    Long id;
    private String nameTwo;
    private int ageTwo;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getNameTwo()
    {
        return nameTwo;
    }

    public void setNameTwo(String nameTwo)
    {
        this.nameTwo = nameTwo;
    }

    public int getAgeTwo()
    {
        return ageTwo;
    }

    public void setAgeTwo(int ageTwo)
    {
        this.ageTwo = ageTwo;
    }

    public int getYearsTwo()
    {
        return yearsTwo;
    }

    public void setYearsTwo(int yearsTwo)
    {
        this.yearsTwo = yearsTwo;
    }

    private int yearsTwo;

    private TestEntityTwo()
    {
    }

    public TestEntityTwo(String name, int age, int years)
    {
        this.nameTwo = name;
        this.ageTwo = age;
        this.yearsTwo = years;
    }
}
