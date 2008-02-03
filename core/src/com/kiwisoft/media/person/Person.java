/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 10:09:20 PM
 */
package com.kiwisoft.media.person;

import java.util.Map;
import java.util.HashMap;

import com.kiwisoft.media.dataImport.SearchManager;
import com.kiwisoft.media.dataImport.SearchPattern;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Identifyable;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;

public class Person extends IDObject
{
	public static final String FIRST_NAME="firstName";
	public static final String MIDDLE_NAME="middleName";
	public static final String SURNAME="surname";
	public static final String NAME="name";
	public static final String GENDER="gender";
	public static final String PICTURE="picture";

	private String firstName;
	private String middleName;
	private String surname;
	private String name;
	private String imdbKey;
	private String tvcomKey;
	private boolean actor;

	public Person()
	{
	}

	public Person(DBDummy dummy)
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

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName=firstName;
		setModified();
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName=middleName;
		setModified();
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname=surname;
		setModified();
	}

	public Gender getGender()
	{
		return (Gender)getReference(GENDER);
	}

	public void setSex(Gender gender)
	{
		setReference(GENDER, gender);
	}

	public Picture getPicture()
	{
		return (Picture)getReference(PICTURE);
	}

	public void setPicture(Picture picture)
	{
		setReference(PICTURE, picture);
	}

	public boolean isActor()
	{
		return actor;
	}

	public void setActor(boolean actor)
	{
		this.actor=actor;
		setModified();
	}


	public String getImdbKey()
	{
		return imdbKey;
	}

	public void setImdbKey(String imdbKey)
	{
		this.imdbKey=imdbKey;
		setModified();
	}

	public String getTvcomKey()
	{
		return tvcomKey;
	}

	public void setTvcomKey(String tvcomKey)
	{
		this.tvcomKey=tvcomKey;
		setModified();
	}

	public String toString()
	{
		return getName();
	}

	public Identifyable loadReference(String name, Object referenceId)
	{
		if (GENDER.equals(name)) return Gender.get((Long)referenceId);
		return super.loadReference(name, referenceId);
	}

	public String getSearchPattern(int type)
	{
		SearchPattern pattern=SearchManager.getInstance().getSearchPattern(type, this);
		if (pattern!=null) return pattern.getPattern();
		else return null;
	}

	public void setSearchPattern(int type, String patternString)
	{
		SearchPattern pattern=SearchManager.getInstance().getSearchPattern(type, this);
		if (StringUtils.isEmpty(patternString))
		{
			if (pattern!=null) pattern.delete();
		}
		else
		{
			if (pattern==null) pattern=new SearchPattern(this, type);
			pattern.setPattern(patternString);
		}
	}

	public boolean isUsed()
	{
		return super.isUsed() || PersonManager.getInstance().isPersonUsed(this);
	}

	public Credits<CastMember> getActingCredits()
	{
		Credits<CastMember> credits=new Credits<CastMember>();
		for (CastMember castMember : DBLoader.getInstance().loadSet(CastMember.class, null, "actor_id=?", getId()))
		{
			Movie movie=castMember.getMovie();
			if (movie!=null)
			{
				credits.addProduction(movie);
				credits.addCredit(movie, castMember);
			}
			Show show=castMember.getShow();
			if (show!=null)
			{
				credits.addProduction(show);
				credits.addCredit(show, castMember);
			}
			Episode episode=castMember.getEpisode();
			if (episode!=null)
			{
				credits.addProduction(episode.getShow(), episode);
				credits.addCredit(episode, castMember);
			}

		}
		return credits;
	}

	public Map<CreditType, Credits<Credit>> getCrewCredits()
	{
		Map<CreditType, Credits<Credit>> creditMap=new HashMap<CreditType, Credits<Credit>>();
		for (Credit crewMember : DBLoader.getInstance().loadSet(Credit.class, null, "person_id=?", getId()))
		{
			Credits<Credit> credits=creditMap.get(crewMember.getCreditType());
			if (credits==null) creditMap.put(crewMember.getCreditType(), credits=new Credits<Credit>());
			Movie movie=crewMember.getMovie();
			if (movie!=null)
			{
				credits.addProduction(movie);
				credits.addCredit(movie, crewMember);
			}
//			Show show=crewMember.getShow();
//			if (show!=null)
//			{
//				creditMap.addProduction(show);
//				creditMap.addCredit(show, crewMember);
//			}
			Episode episode=crewMember.getEpisode();
			if (episode!=null)
			{
				credits.addProduction(episode.getShow(), episode);
				credits.addCredit(episode, crewMember);
			}
		}
		return creditMap;
	}

}
