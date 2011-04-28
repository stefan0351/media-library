package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.Person;

import java.util.Collections;

public class CrewData extends CastData
{
	private static final long serialVersionUID=-2065122511217698103L;
	
	private CreditType type;
	private String subType;
	private Credit credit;

	public CrewData()
	{
	}

	public CrewData(Credit crewMember)
	{
		credit=crewMember;
		Person person=crewMember.getPerson();
		setName(person.getName());
		setKey(person.getImdbKey());
		setPersons(Collections.singleton(person));
		type=crewMember.getCreditType();
		subType=crewMember.getSubType();
	}

	public CrewData(String name, CreditType type, String subType, String key)
	{
		setKey(key);
		setName(name);
		this.type=type;
		this.subType=subType;
	}

	public CreditType getType()
	{
		return type;
	}

	public String getSubType()
	{
		return subType;
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

		return !(getName()!=null ? !getName().equals(crewData.getName()) : crewData.getName()!=null) &&
			   !(subType!=null ? !subType.equals(crewData.subType) : crewData.subType!=null) &&
			   !(type!=null ? !type.equals(crewData.type) : crewData.type!=null);
	}

	@Override
	public int hashCode()
	{
		int result;
		result=(getName()!=null ? getName().hashCode() : 0);
		result=29*result+(type!=null ? type.hashCode() : 0);
		result=29*result+(subType!=null ? subType.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "CrewData{"+
			   "name='"+getName()+'\''+
			   ", type="+type+
			   ", subType='"+subType+'\''+
			   ", key='"+getKey()+'\''+
			   '}';
	}

	public void setCredit(Credit credit)
	{
		this.credit=credit;
	}

	public Credit getCredit()
	{
		return credit;
	}
}
