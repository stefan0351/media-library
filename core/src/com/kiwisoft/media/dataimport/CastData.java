package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Person;

import java.util.Collections;

/**
 * @author Stefan Stiller
 */
public class CastData extends CreditData
{
	private static final long serialVersionUID=-1107097608838199653L;

	private Integer creditOrder;
	private String role;

	private CastMember castMember;

	public CastData()
	{
	}

	public CastData(CastMember castMember)
	{
		this.castMember=castMember;
		Person person=castMember.getActor();
		setName(person.getName());
		setKey(person.getImdbKey());
		if (castMember.getActor()!=null) setPersons(Collections.singleton(castMember.getActor()));
		else setPersons(Collections.<Person>emptySet());
		role=castMember.getCharacterName();
	}

	public CastData(String name, String role, Integer creditOrder, String key)
	{
		setName(name);
		setKey(key);
		this.role=role;
		this.creditOrder=creditOrder;
	}

	public Integer getCreditOrder()
	{
		return creditOrder;
	}

	public String getRole()
	{
		return role;
	}


	public void setCreditOrder(Integer creditOrder)
	{
		this.creditOrder=creditOrder;
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

		return !(getName()!=null ? !getName().equals(castData.getName()) : castData.getName()!=null)
			   && !(role!=null ? !role.equals(castData.role) : castData.role!=null);
	}

	@Override
	public int hashCode()
	{
		int result;
		result=(getName()!=null ? getName().hashCode() : 0);
		result=29*result+(role!=null ? role.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "CastData{"+
			   "creditOrder="+creditOrder+
			   ", actor='"+getName()+'\''+
			   ", role='"+role+'\''+
			   ", key='"+getKey()+'\''+
			   '}';
	}

	public void setCastMember(CastMember castMember)
	{
		this.castMember=castMember;
		if (castMember.getActor()!=null) setPersons(Collections.singleton(castMember.getActor()));
	}

	public CastMember getCastMember()
	{
		return castMember;
	}
}
