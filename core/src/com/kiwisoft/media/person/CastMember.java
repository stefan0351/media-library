/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 18, 2003
 * Time: 11:21:04 AM
 */
package com.kiwisoft.media.person;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;

public class CastMember extends IDObject
{
	public static final String ACTOR="actor";
	public static final String SHOW="show";
	public static final String MOVIE="movie";
	public static final String EPISODE="episode";
	public static final String VOICE="voice";
	public static final String CHARACTER_NAME="characterName";
	public static final String CREDIT_TYPE="creditType";
	public static final String PICTURE="picture";

	private String voice;
	private String characterName;
	private String description;
	private Integer creditOrder;

	public CastMember()
	{
	}

	public CastMember(DBDummy dummy)
	{
		super(dummy);
	}

	public Person getActor()
	{
		return (Person)getReference(ACTOR);
	}

	public void setActor(Person value)
	{
		setReference(ACTOR, value);
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show value)
	{
		setReference(SHOW, value);
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

	public MediaFile findPicture()
	{
		MediaFile picture=getPicture();
		if (picture==null && getActor()!=null) picture=getActor().getPicture();
		return picture;
	}

	public MediaFile getPicture()
	{
		return (MediaFile)getReference(PICTURE);
	}

	public void setPicture(MediaFile picture)
	{
		setReference(PICTURE, picture);
	}

	public CreditType getCreditType()
	{
		return (CreditType)getReference(CREDIT_TYPE);
	}

	public void setCreditType(CreditType creditType)
	{
		setReference(CREDIT_TYPE, creditType);
	}

	public Integer getCreditOrder()
	{
		return creditOrder;
	}

	public void setCreditOrder(Integer creditOrder)
	{
		Integer oldCreditOrder=this.creditOrder;
		this.creditOrder=creditOrder;
		setModified("creditOrder", oldCreditOrder, this.creditOrder);
	}

	public String getCharacterName()
	{
		return characterName;
	}

	public void setCharacterName(String characterName)
	{
		String oldCharacterName=getCharacterName();
		this.characterName=characterName;
		setModified(CHARACTER_NAME, oldCharacterName, characterName);
	}

	public String getVoice()
	{
		return voice;
	}

	public void setVoice(String voice)
	{
		String oldVoice=getVoice();
		this.voice=voice;
		setModified(VOICE, oldVoice, voice);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		String oldDescription=this.description;
		this.description=description;
		setModified("description", oldDescription, this.description);
	}

	@Override
	public String toString()
	{
		return getCharacterName()+" ("+getActor()+")";
	}

	public Production getProduction()
	{
		Production production=getEpisode();
		if (production==null) production=getShow();
		if (production==null) production=getMovie();
		return production;
	}
}
