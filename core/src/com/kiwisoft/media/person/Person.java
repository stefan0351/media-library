/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 10:09:20 PM
 */
package com.kiwisoft.media.person;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.kiwisoft.media.dataImport.SearchManager;
import com.kiwisoft.media.dataImport.SearchPattern;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.Name;
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
	private Set<Name> altNames;
	private String imdbKey;
	private String tvcomKey;

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
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, this.name);
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		String oldFirstName=this.firstName;
		this.firstName=firstName;
		setModified(FIRST_NAME, oldFirstName, this.firstName);
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		String oldMiddleName=this.middleName;
		this.middleName=middleName;
		setModified(MIDDLE_NAME, oldMiddleName, this.middleName);
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		String oldSurname=this.surname;
		this.surname=surname;
		setModified(SURNAME, oldSurname, this.surname);
	}

	public Name createAltName()
	{
		Name altName=new Name(this);
		getAltNames().add(altName);
		return altName;
	}

	public void dropAltName(Name name)
	{
		if (altNames!=null) altNames.remove(name);
		name.delete();
	}

	public Set<Name> getAltNames()
	{
		if (altNames==null) altNames=DBLoader.getInstance().loadSet(Name.class, null, "type=? and ref_id=?", Name.SHOW, getId());
		return altNames;
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

	public String getImdbKey()
	{
		return imdbKey;
	}

	public void setImdbKey(String imdbKey)
	{
		String oldImdbKey=this.imdbKey;
		this.imdbKey=imdbKey;
		setModified("imdbKey", oldImdbKey, this.imdbKey);
	}

	public String getTvcomKey()
	{
		return tvcomKey;
	}

	public void setTvcomKey(String tvcomKey)
	{
		String oldTvcomKey=this.tvcomKey;
		this.tvcomKey=tvcomKey;
		setModified("tvcomKey", oldTvcomKey, this.tvcomKey);
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

	public boolean isUsed()
	{
		return super.isUsed() || PersonManager.getInstance().isPersonUsed(this);
	}

	public Credits<CastMember> getSortedActingCredits()
	{
		Credits<CastMember> credits=new Credits<CastMember>();
		for (CastMember castMember : getActingCredits())
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

	public Set<CastMember> getActingCredits()
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "actor_id=?", getId());
	}

	public Map<CreditType, Credits<Credit>> getSortedCrewCredits()
	{
		Map<CreditType, Credits<Credit>> creditMap=new HashMap<CreditType, Credits<Credit>>();
		for (Credit crewMember : getCrewCredits())
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

	public Set<Credit> getCrewCredits()
	{
		return DBLoader.getInstance().loadSet(Credit.class, null, "person_id=?", getId());
	}

}
