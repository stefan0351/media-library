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
		String oldName=this.name;
		this.name=name;
		setModified("name", oldName, this.name);
	}

	@Override
	public String toString()
	{
		if (name!=null) return name;
		return super.toString();
	}

	@SuppressWarnings({"unchecked"})
	public Set<Show> getShows()
	{
		return (Set<Show>)DBAssociation.getAssociation(Genre.class, SHOWS).getAssociations(this);
	}
}
