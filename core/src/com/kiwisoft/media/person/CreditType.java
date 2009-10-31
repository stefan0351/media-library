package com.kiwisoft.media.person;

import java.util.*;
import java.io.Serializable;

import com.kiwisoft.utils.Identifyable;

public class CreditType implements Identifyable, Comparable<CreditType>, Serializable
{
	private static final long serialVersionUID=-5770869194813731803L;

	private static final Map<Long, CreditType> map=new HashMap<Long, CreditType>(3);
	private static final Set<CreditType> nonCastTypes=new HashSet<CreditType>();

	public static final CreditType WRITER=new CreditType(1L, "Writing credits", "Writer", false);
	public static final CreditType DIRECTOR=new CreditType(2L, "Directed by", "Director", false);
	public static final CreditType PRODUCER=new CreditType(3L, "Produced by", "Producer", false);
	public static final CreditType COMPOSER=new CreditType(4L, "Original Music by", "Composer", false);
	public static final CreditType EDITOR=new CreditType(5L, "Film Editing by", "Editor", false);
	public static final CreditType ART_DIRECTOR=new CreditType(6L, "Art Direction by", "Art Director", false);
	public static final CreditType CINEMATOGRAPHER=new CreditType(7L, "Cinematography by", "Cinematography", false);
	public static final CreditType MAIN_CAST=new CreditType(8L, "Main Cast", "Main Cast", true);
	public static final CreditType RECURRING_CAST=new CreditType(9L, "Recurring Cast", "Recurring Cast", true);
	public static final CreditType GUEST_CAST=new CreditType(10L, "Guest Gast", "Guest Cast", true);
	public static final CreditType INTERPRET=new CreditType(11L, "Interpret", "Interpret", false);
	public static final CreditType ACTOR=new CreditType(12L, "Actor/Actress", "Actor/Actress", true);

	public static CreditType valueOf(Long id)
	{
		if (id!=null && id.longValue()==12L) return ACTOR;
		return map.get(id);
	}

	public static Collection<CreditType> values()
	{
		return map.values();	
	}

	public static Collection<CreditType> noCastValues()
	{
		return nonCastTypes;
	}

	private Long id;
	private String byName;
	private String asName;
	private boolean actingCredit;

	private CreditType(Long id, String byName, String asName, boolean cast)
	{
		this.id=id;
		this.byName=byName;
		this.asName=asName;
		this.actingCredit=cast;
		if (id.longValue()!=12L)
		{
			map.put(id, this);
			if (!cast) nonCastTypes.add(this);
		}
	}

	@Override
	public Object getPrimaryKey()
	{
		return getId();
	}
	
	public Long getId()
	{
		return id;
	}

	public String getAsName()
	{
		return asName;
	}

	public String getByName()
	{
		return byName;
	}

	public boolean isActingCredit()
	{
		return actingCredit;
	}

	@Override
	public String toString()
	{
		return getAsName();
	}

	@Override
	public int compareTo(CreditType creditType)
	{
		return getId().compareTo(creditType.getId());
	}
}
