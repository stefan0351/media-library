/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 18, 2003
 * Time: 11:21:04 AM
 */
package com.kiwisoft.media.person;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.person.Person;

public class CastMember extends IDObject
{
	public static final int MAIN_CAST=1;
	public static final int RECURRING_CAST=2;
	public static final int GUEST_CAST=3;

	public static final String ACTOR="actor";
	public static final String SHOW="show";
	public static final String EPISODE="episode";
	public static final String TYPE="type";
	public static final String VOICE="voice";
	public static final String CHARACTER_NAME="characterName";

	private int type;
	private String voice;
	private String imageSmall;
	private String imageLarge;
	private String characterName;
	private String description;

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

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		int oldType=getType();
		this.type=type;
		setModified();
		firePropertyChange(TYPE, oldType, type);
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

	public String getImageSmall()
	{
		return imageSmall;
	}

	public void setImageSmall(String imageSmall)
	{
		this.imageSmall=imageSmall;
		setModified();
	}

	public String getImageLarge()
	{
		return imageLarge;
	}

	public void setImageLarge(String imageLarge)
	{
		this.imageLarge=imageLarge;
		setModified();
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
}
