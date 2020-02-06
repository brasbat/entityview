package org.brasbat.entityviewdemoapp.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class TestColumnTypes
{
    @Id
    @GeneratedValue
    Long id;
    String stringcol;
    boolean booleanCol;
    int intCol;
    long longCol;
    float floatCol;
    TestEnum testEnum;
    Date testDate;
    TestObjectDto testNestedObject;


    public Long getId()
    {
        return id;
    }

    public String getStringcol()
    {
        return stringcol;
    }

    public void setStringcol(String stringcol)
    {
        this.stringcol = stringcol;
    }

    public boolean getBooleanCol()
    {
        return booleanCol;
    }

    public void setBooleanCol(boolean booleanCol)
    {
        this.booleanCol = booleanCol;
    }

    public int getIntCol()
    {
        return intCol;
    }

    public void setIntCol(int intCol)
    {
        this.intCol = intCol;
    }

    public long getLongCol()
    {
        return longCol;
    }

    public void setLongCol(long longCol)
    {
        this.longCol = longCol;
    }

    public TestEnum getTestEnum()
    {
        return testEnum;
    }

    public void setTestEnum(TestEnum testEnum)
    {
        this.testEnum = testEnum;
    }

    public Date getTestDate()
    {
        return testDate;
    }

    public void setTestDate(Date testDate)
    {
        this.testDate = testDate;
    }


    public float getFloatCol()
    {
        return floatCol;
    }

    public void setFloatCol(float floatCol)
    {
        this.floatCol = floatCol;
    }

	public void setId(Long id)
	{
		this.id = id;
	}

	public boolean isBooleanCol()
	{
		return booleanCol;
	}

	public TestObjectDto getTestNestedObject()
	{
		return testNestedObject;
	}

	public void setTestNestedObject(TestObjectDto testNestedObject)
	{
		this.testNestedObject = testNestedObject;
	}
}
