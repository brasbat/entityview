package org.brasbat.entityview.entityviewreact.entity;

import javax.persistence.Embeddable;

@Embeddable
public class TestObjectDto
{
	private int key;
	private String name;
	private boolean active;

	public int getKey()
	{
		return key;
	}

	public void setKey(int key)
	{
		this.key = key;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
}
