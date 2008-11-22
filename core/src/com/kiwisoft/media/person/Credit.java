package com.kiwisoft.media.person;

import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.medium.Song;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;

public class Credit extends IDObject
{
	public static final String PERSON="person";
	public static final String MOVIE="movie";
	public static final String EPISODE="episode";
	public static final String SONG="song";
	public static final String CREDIT_TYPE="creditType";

	private String subType;

	public Credit()
	{
	}

	public Credit(DBDummy dummy)
	{
		super(dummy);
	}

	public Person getPerson()
	{
		return (Person)getReference(PERSON);
	}

	public void setPerson(Person value)
	{
		setReference(PERSON, value);
	}

	public Episode getEpisode()
	{
		return (Episode)getReference(EPISODE);
	}

	public void setEpisode(Episode value)
	{
		setReference(EPISODE, value);
	}

	public Movie getMovie()
	{
		return (Movie)getReference(MOVIE);
	}

	public void setMovie(Movie value)
	{
		setReference(MOVIE, value);
	}

	public CreditType getCreditType()
	{
		return (CreditType)getReference(CREDIT_TYPE);
	}

	public void setCreditType(CreditType creditType)
	{
		setReference(CREDIT_TYPE, creditType);
	}

	public Song getSong()
	{
		return (Song)getReference(SONG);
	}

	public void setSong(Song song)
	{
		setReference(SONG, song);
	}

	public String getSubType()
	{
		return subType;
	}

	public void setSubType(String subType)
	{
		String oldSubType=this.subType;
		this.subType=subType;
		setModified("subType", oldSubType, this.subType);
	}

	public Production getProduction()
	{
		Production production=getEpisode();
		if (production==null) production=getMovie();
		if (production==null) production=getSong();
		return production;
	}
}
