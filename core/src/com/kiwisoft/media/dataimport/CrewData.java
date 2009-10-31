package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.Person;

import java.io.Serializable;

public class CrewData implements Serializable
{
	private static final long serialVersionUID=-2065122511217698103L;
	
	private String name;
	private CreditType type;
	private String subType;
	private String imdbKey;
	private Person person;

	public CrewData()
	{
	}

	public CrewData(Credit crewMember)
	{
		Person person=crewMember.getPerson();
		name=person.getName();
		imdbKey=person.getImdbKey();
		type=crewMember.getCreditType();
		subType=crewMember.getSubType();
	}

	public CrewData(String name, CreditType type, String subType, String imdbKey)
	{
		this.name=name;
		this.type=type;
		this.subType=subType;
		this.imdbKey=imdbKey;
	}


	public String getImdbKey()
	{
		return imdbKey;
	}


	public String getName()
	{
		return name;
	}

	public CreditType getType()
	{
		return type;
	}

	public String getSubType()
	{
		return subType;
	}

	public void setImdbKey(String imdbKey)
	{
		this.imdbKey=imdbKey;
	}

	public void setName(String name)
	{
		this.name=name;
	}

	public void setSubType(String subType)
	{
		this.subType=subType;
	}

	public void setType(CreditType type)
	{
		this.type=type;
	}

	public Long getTypeId()
	{
		return type!=null? type.getId() : null;
	}

	public void setTypeId(Long typeId)
	{
		if (typeId!=null) type=CreditType.valueOf(typeId);
		else type=null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final CrewData crewData=(CrewData)o;

		return !(name!=null ? !name.equals(crewData.name) : crewData.name!=null) &&
			   !(subType!=null ? !subType.equals(crewData.subType) : crewData.subType!=null) &&
			   !(type!=null ? !type.equals(crewData.type) : crewData.type!=null);
	}

	@Override
	public int hashCode()
	{
		int result;
		result=(name!=null ? name.hashCode() : 0);
		result=29*result+(type!=null ? type.hashCode() : 0);
		result=29*result+(subType!=null ? subType.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "CrewData{"+
			   "name='"+name+'\''+
			   ", type="+type+
			   ", subType='"+subType+'\''+
			   ", imdbKey='"+imdbKey+'\''+
			   '}';
	}

	public void setPerson(Person person)
	{
		this.person=person;
	}

	public Person getPerson()
	{
		return person;
	}
}
