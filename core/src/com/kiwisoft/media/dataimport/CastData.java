package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Person;

import java.io.Serializable;

/**
 * @author Stefan Stiller
 */
public class CastData implements Serializable
{
	private static final long serialVersionUID=-1107097608838199653L;
	
	private String actor;
	private String role;
	private Integer creditOrder;
	private String key;

	private Person person;
	private CastMember castMember;

	public CastData()
	{
	}

	public CastData(CastMember castMember)
	{
		Person person=castMember.getActor();
		actor=person.getName();
		key=person.getImdbKey();
		role=castMember.getCharacterName();
	}

	public CastData(String actor, String role, Integer creditOrder, String key)
	{
		this.actor=actor;
		this.role=role;
		this.creditOrder=creditOrder;
		this.key=key;
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


	public String getKey()
	{
		return key;
	}

	public void setActor(String actor)
	{
		this.actor=actor;
	}

	public void setCreditOrder(Integer creditOrder)
	{
		this.creditOrder=creditOrder;
	}

	public void setKey(String key)
	{
		this.key=key;
	}

	public void setRole(String role)
	{
		this.role=role;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final CastData castData=(CastData)o;

		return !(actor!=null ? !actor.equals(castData.actor) : castData.actor!=null)
			   && !(role!=null ? !role.equals(castData.role) : castData.role!=null);
	}

	@Override
	public int hashCode()
	{
		int result;
		result=(actor!=null ? actor.hashCode() : 0);
		result=29*result+(role!=null ? role.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "CastData{"+
			   "creditOrder="+creditOrder+
			   ", actor='"+actor+'\''+
			   ", role='"+role+'\''+
			   ", key='"+key+'\''+
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

	public void setCastMember(CastMember castMember)
	{
		this.castMember=castMember;
	}

	public CastMember getCastMember()
	{
		return castMember;
	}
}
