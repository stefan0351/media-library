/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 10:09:20 PM
 */
package com.kiwisoft.media;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.Identifyable;

public class Person extends IDObject
{
	public static final String FIRST_NAME="firstName";
	public static final String MIDDLE_NAME="middleName";
	public static final String SURNAME="surname";
	public static final String NAME="name";
	public static final String SEX="sex";

	private String firstName;
	private String middleName;
	private String surname;
	private String name;
	private boolean actor;

	public Person()
	{
	}

	public Person(DBDummy dummy)
	{
		super(dummy);
	}


	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
		setModified();
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName=firstName;
		setModified();
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName=middleName;
		setModified();
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname=surname;
		setModified();
	}

	public Sex getSex()
	{
		return (Sex)getReference(SEX);
	}

	public void setSex(Sex sex)
	{
		setReference(SEX, sex);
	}

	public boolean isActor()
	{
		return actor;
	}

	public void setActor(boolean actor)
	{
		this.actor=actor;
		setModified();
	}

	public String toString()
	{
		return getName();
	}

	public Identifyable loadReference(String name, Long referenceId)
	{
		if (SEX.equals(name)) return Sex.get(referenceId);
		return super.loadReference(name, referenceId);
	}

	public String getSearchPattern(int type)
	{
		SearchPattern pattern=SearchManager.getInstance().getSearchPattern(type, this);
		if (pattern!=null) return pattern.getPattern();
		else return null;
	}

	public void setSearchPattern(int type, String patternString)
	{
		SearchPattern pattern=SearchManager.getInstance().getSearchPattern(type, this);
		if (StringUtils.isEmpty(patternString))
		{
			if (pattern!=null) pattern.delete();
		}
		else
		{
			if (pattern==null) pattern=new SearchPattern(this, type);
			pattern.setPattern(patternString);
		}
	}

	public boolean isUsed()
	{
		return super.isUsed() || PersonManager.getInstance().isPersonUsed(this);
	}
}
