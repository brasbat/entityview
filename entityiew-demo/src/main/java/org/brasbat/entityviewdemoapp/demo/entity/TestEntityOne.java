package org.brasbat.entityviewdemoapp.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TestEntityOne
{
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int age;
    private int years;

    public TestEntityOne()
    {
    }

    public TestEntityOne(String name, int age, int years)
    {
        this.name = name;
        this.age = age;
        this.years = years;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getYears()
    {
        return years;
    }

    public void setYears(int years)
    {
        this.years = years;
    }
}
