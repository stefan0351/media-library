package com.kiwisoft.media.person;

import java.util.Map;
import java.util.HashMap;

import com.kiwisoft.utils.Identifyable;

public class CreditType implements Identifyable, Comparable<CreditType>
{
	public static final Map<Long, CreditType> map=new HashMap<Long, CreditType>(3);

	public static final CreditType WRITER=new CreditType(1L, "Writing credits", "Writer");
	public static final CreditType DIRECTOR=new CreditType(2L, "Directed by", "Director");
	public static final CreditType PRODUCER=new CreditType(3L, "Produced by", "Producer");
	public static final CreditType COMPOSER=new CreditType(4L, "Original Music by", "Composer");
	public static final CreditType EDITOR=new CreditType(5L, "Film Editing by", "Editor");
	public static final CreditType ART_DIRECTOR=new CreditType(6L, "Art Direction by", "Art Director");
	public static final CreditType CINEMATOGRAPHER=new CreditType(7L, "Cinematography by", "Cinematography");
	public static final CreditType MAIN_CAST=new CreditType(8L, "Main Cast", "Main Cast");
	public static final CreditType RECURRING_CAST=new CreditType(9L, "Recurring Cast", "Recurring Cast");
	public static final CreditType GUEST_CAST=new CreditType(10L, "Guest Gast", "Guest Cast");
	public static final CreditType INTERPRET=new CreditType(11L, "Interpret", "Interpret");

	public static CreditType get(Long id)
	{
		return map.get(id);
	}

	private Long id;
	private String byName;
	private String asName;

	private CreditType(Long id, String byName, String asName)
	{
		this.id=id;
		this.byName=byName;
		this.asName=asName;
		map.put(id, this);
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

	public int compareTo(CreditType creditType)
	{
		return getId().compareTo(creditType.getId());
	}
}
