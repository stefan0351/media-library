package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Person;

/**
 * @author Stefan Stiller
 */
public class CastData
{
	private String actor;
	private String role;
	private Integer creditOrder;
	private String imdbKey;

	public CastData(CastMember castMember)
	{
		Person person=castMember.getActor();
		actor=person.getName();
		imdbKey=person.getImdbKey();
		role=castMember.getCharacterName();
	}

	public CastData(String actor, String role, Integer creditOrder, String imdbKey)
	{
		this.actor=actor;
		this.role=role;
		this.creditOrder=creditOrder;
		this.imdbKey=imdbKey;
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


	public String getImdbKey()
	{
		return imdbKey;
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final CastData castData=(CastData)o;

		return !(actor!=null ? !actor.equals(castData.actor) : castData.actor!=null)
			   && !(role!=null ? !role.equals(castData.role) : castData.role!=null);
	}

	public int hashCode()
	{
		int result;
		result=(actor!=null ? actor.hashCode() : 0);
		result=29*result+(role!=null ? role.hashCode() : 0);
		return result;
	}
}
