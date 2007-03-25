package com.kiwisoft.media.dataImport;

import com.kiwisoft.media.person.CrewMember;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.Person;

public class CrewData
{
	private String name;
	private CreditType type;
	private String subType;
	private String imdbKey;

	public CrewData(CrewMember crewMember)
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

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final CrewData crewData=(CrewData)o;

		return !(name!=null ? !name.equals(crewData.name) : crewData.name!=null) &&
			   !(subType!=null ? !subType.equals(crewData.subType) : crewData.subType!=null) &&
			   !(type!=null ? !type.equals(crewData.type) : crewData.type!=null);
	}

	public int hashCode()
	{
		int result;
		result=(name!=null ? name.hashCode() : 0);
		result=29*result+(type!=null ? type.hashCode() : 0);
		result=29*result+(subType!=null ? subType.hashCode() : 0);
		return result;
	}
}
