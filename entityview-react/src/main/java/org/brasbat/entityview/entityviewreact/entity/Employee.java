package org.brasbat.entityview.entityviewreact.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Employee
{
    private @Id
    @GeneratedValue
    Long id;
    private String name;
    private int age;
    private int years;

    private Employee() {}

    public Employee(String name, int age, int years) {
        this.name = name;
        this.age = age;
        this.years = years;
    }
}
