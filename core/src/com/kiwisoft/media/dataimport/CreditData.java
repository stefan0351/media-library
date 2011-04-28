package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.person.Person;
import com.kiwisoft.utils.Bean;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 26.02.11
 */
public class CreditData extends Bean implements Serializable
{
	private String name;
	private String listedAs;
	private String key;

	private Set<Person> persons;

	public String getName()
	{
		return name;
	}

	public String getKey()
	{
		return key;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		firePropertyChange("name", oldName, name);
	}

	public void setKey(String key)
	{
		String oldKey=this.key;
		this.key=key;
		firePropertyChange("key", oldKey, key);
	}

	public void setListedAs(String listedAs)
	{
		String oldListedAs=this.listedAs;
		this.listedAs=listedAs;
		firePropertyChange("listedAs", oldListedAs, listedAs);
	}

	public String getListedAs()
	{
		return listedAs;
	}

	public Set<Person> getPersons()
	{
		return persons;
	}

	public void setPersons(Set<Person> persons)
	{
		this.persons=persons;
	}
}
