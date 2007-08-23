package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBAssociation;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;

public class Genre extends IDObject
{
	public static final String SHOWS="shows";
	public static final String NAME="name";

	private static final DBAssociation<Genre, Show> ASSOCIATION_SHOWS=DBAssociation.getAssociation(SHOWS, Genre.class, Show.class);

	private String name;

	public Genre()
	{
	}

	public Genre(DBDummy dummy)
	{
		super(dummy);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
		setModified();
	}

	public String toString()
	{
		if (name!=null) return name;
		return super.toString();
	}

	public Set<Show> getShows()
	{
		return ASSOCIATION_SHOWS.getAssociations(this);
	}
}
