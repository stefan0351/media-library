package com.kiwisoft.media.dataImport;

import com.kiwisoft.media.person.CrewMember;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 05.03.2007
 * Time: 16:52:40
 * To change this template use File | Settings | File Templates.
 */
public class CrewData
{
	private String name;
	private String type;
	private String subType;

	public CrewData(CrewMember crewMember)
	{
		name=crewMember.getPerson().getName();
		type=crewMember.getType();
		subType=crewMember.getSubType();
	}

	public CrewData(String name, String type, String subType)
	{
		this.name=name;
		this.type=type;
		this.subType=subType;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
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

		if (name!=null ? !name.equals(crewData.name) : crewData.name!=null) return false;
		if (subType!=null ? !subType.equals(crewData.subType) : crewData.subType!=null) return false;
		return !(type!=null ? !type.equals(crewData.type) : crewData.type!=null);
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
