package com.kiwisoft.media;

import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.media.show.Episode;

public class CrewMember extends IDObject
{
	public static final String PERSON="person";
	public static final String EPISODE="episode";
	public static final String TYPE="type";

	public static final String WRITER = "Writer";
	public static final String DIRECTOR = "Director";
	public static final String STORY = "Story";

	private String type;

	public CrewMember()
	{
	}

	public CrewMember(DBDummy dummy)
	{
		super(dummy);
	}

	public Person getPerson()
	{
		return (Person)getReference(CrewMember.PERSON);
	}

	public void setPerson(Person value)
	{
		setReference(CrewMember.PERSON, value);
	}

	public Episode getEpisode()
	{
		return (Episode)getReference(CrewMember.EPISODE);
	}

	public void setEpisode(Episode value)
	{
		setReference(CrewMember.EPISODE, value);
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
