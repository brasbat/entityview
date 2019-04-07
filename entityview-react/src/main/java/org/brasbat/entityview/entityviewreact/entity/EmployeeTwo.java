package org.brasbat.entityview.entityviewreact.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class EmployeeTwo
{
    private @Id
    @GeneratedValue
    Long id;
    private String nameTwo;
    private int ageTwo;

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

    private EmployeeTwo()
    {
    }

    public EmployeeTwo(String name, int age, int years)
    {
        this.nameTwo = name;
        this.ageTwo = age;
        this.yearsTwo = years;
    }
}
