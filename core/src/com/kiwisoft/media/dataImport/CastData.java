package com.kiwisoft.media.dataImport;

import com.kiwisoft.media.person.CastMember;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 05.03.2007
 * Time: 16:53:05
 * To change this template use File | Settings | File Templates.
 */
public class CastData
{
	private String actor;
	private String role;
	private Integer creditOrder;

	public CastData(CastMember castMember)
	{
		actor=castMember.getActor().getName();
		role=castMember.getCharacterName();
	}

	public CastData(String actor, String role, Integer creditOrder)
	{
		this.actor=actor;
		this.role=role;
		this.creditOrder=creditOrder;
	}

	public Integer getCreditOrder()
	{
		return creditOrder;
	}

	public String getActor()
	{
		return actor;
	}

	public String getRole()
	{
		return role;
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final CastData castData=(CastData)o;

		if (actor!=null ? !actor.equals(castData.actor) : castData.actor!=null) return false;
		return !(role!=null ? !role.equals(castData.role) : castData.role!=null);
	}

	public int hashCode()
	{
		int result;
		result=(actor!=null ? actor.hashCode() : 0);
		result=29*result+(role!=null ? role.hashCode() : 0);
		return result;
	}
}
