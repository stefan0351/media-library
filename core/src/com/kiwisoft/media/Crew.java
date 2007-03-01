package com.kiwisoft.media;

import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.media.show.Episode;

public class Crew extends IDObject
{
	public static final String PERSON="person";
	public static final String EPISODE="episode";
	public static final String TYPE="type";

	private String type;

	public Crew()
	{
	}

	public Crew(DBDummy dummy)
	{
		super(dummy);
	}

	public Person getPerson()
	{
		return (Person)getReference(Crew.PERSON);
	}

	public void setPerson(Person value)
	{
		setReference(Crew.PERSON, value);
	}

	public Episode getEpisode()
	{
		return (Episode)getReference(Crew.EPISODE);
	}

	public void setEpisode(Episode value)
	{
		setReference(Crew.EPISODE, value);
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		String oldType=getType();
		this.type=type;
		setModified();
		firePropertyChange(TYPE, oldType, type);
	}
}
