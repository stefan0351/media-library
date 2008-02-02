/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 18, 2003
 * Time: 11:21:04 AM
 */
package com.kiwisoft.media.person;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.utils.Identifyable;
import com.kiwisoft.utils.StringUtils;

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

	public Picture getPicture()
	{
		return (Picture)getReference(PICTURE);
	}

	public void setPicture(Picture picture)
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
		this.creditOrder=creditOrder;
		setModified();
	}

	public String getCharacterName()
	{
		return characterName;
	}

	public void setCharacterName(String characterName)
	{
		String oldCharacterName=getCharacterName();
		this.characterName=characterName;
		setModified();
		firePropertyChange(CHARACTER_NAME, oldCharacterName, characterName);
	}

	public String getVoice()
	{
		return voice;
	}

	public void setVoice(String voice)
	{
		String oldVoice=getVoice();
		this.voice=voice;
		setModified();
		firePropertyChange(VOICE, oldVoice, voice);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description=description;
		setModified();
	}

	public String toString()
	{
		return getCharacterName()+" ("+getActor()+")";
	}

	public Identifyable loadReference(String name, Long referenceId)
	{
		if (CREDIT_TYPE.equals(name)) return CreditType.get(referenceId);
		return super.loadReference(name, referenceId);
	}

	public static class Comparator implements java.util.Comparator<CastMember>
	{
		public int compare(CastMember o1, CastMember o2)
		{
			String name1=o1.getActor()!=null ? o1.getActor().getName() : null;
			String name2=o2.getActor()!=null ? o2.getActor().getName() : null;
			int result=StringUtils.compareIgnoreCase(name1, name2, false);
			if (result==0) result=o1.getId().compareTo(o2.getId());
			return result;
		}
	}

	public static class CreditComparator implements java.util.Comparator<CastMember>
	{
		public int compare(CastMember castMember1, CastMember castMember2)
		{
			Integer creditOrder2=castMember2.getCreditOrder();
			Integer creditOrder1=castMember1.getCreditOrder();
			if (creditOrder1!=null && creditOrder2!=null) return creditOrder1.compareTo(creditOrder2);
			else if (creditOrder1!=null) return -1;
			else if (creditOrder2!=null) return 1;
			return castMember1.getActor().getName().compareToIgnoreCase(castMember2.getActor().getName());
		}
	}
}
