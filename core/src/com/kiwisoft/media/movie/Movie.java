/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.movie;

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBAssociation;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Summary;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.*;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.video.Recordable;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.person.CrewMember;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CreditType;

public class Movie extends IDObject implements Recordable, Production
{
	public static final String SHOW="show";
	public static final String DEFAULT_INFO="defaultInfo";
	public static final String GENRES="genres";
	public static final String LANGUAGES="languages";
	public static final String COUNTRIES="countries";
	public static final String INDEX_BY="indexBy";
	public static final String POSTER="poster";

	private static final DBAssociation<Movie, Genre> ASSOCIATION_GENRES
		=DBAssociation.getAssociation(GENRES, Movie.class, Genre.class);
	private static final DBAssociation<Movie, Language> ASSOCIATION_LANGUAGES
		=DBAssociation.getAssociation(LANGUAGES, Movie.class, Language.class);
	private static final DBAssociation<Movie, Country> ASSOCIATION_COUNTRIES
		=DBAssociation.getAssociation(COUNTRIES, Movie.class, Country.class);

	private String title;
	private String germanTitle;
	private boolean record;
	private Set<Name> altNames;
	private String javaScript;
	private String webScriptFile;
	private Integer year;
	private Integer runtime;
	private String indexBy;
	private String imdbKey;

	public Movie()
	{
	}

	public Movie(Show show)
	{
		setShow(show);
	}

	public Movie(DBDummy dummy)
	{
		super(dummy);
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

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
		setModified();
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public void setGermanTitle(String germanTitle)
	{
		this.germanTitle=germanTitle;
		setModified();
	}

	public String getTitle(Language language)
	{
		if (language!=null)
		{
			if ("de".equals(language.getSymbol()) && !StringUtils.isEmpty(getGermanTitle())) return getGermanTitle();
			if (getLanguages().contains(language)) return getTitle();
			else
			{
				for (Iterator it=getAltNames().iterator(); it.hasNext();)
				{
					Name altName=(Name)it.next();
					if (altName.getLanguage()==language) return altName.getName();
				}
			}
		}
		return getTitle();
	}

	public String getIndexBy(Language language)
	{
		String indexBy=null;
		if (language!=null)
		{
			if ("de".equals(language.getSymbol()) && !StringUtils.isEmpty(getGermanTitle()))
			{
				indexBy=IndexByUtils.createGermanIndexBy(getGermanTitle());
			}
			else if (getLanguages().contains(language))
			{
				indexBy=getIndexBy();
			}
			else
			{
				for (Iterator it=getAltNames().iterator(); it.hasNext();)
				{
					Name altName=(Name)it.next();
					if (altName.getLanguage()==language) indexBy=IndexByUtils.createIndexBy(altName.getName());
				}
			}
		}
		if (StringUtils.isEmpty(indexBy))
		{
			indexBy=getIndexBy();
		}
		return indexBy;
	}

	public Name createAltName()
	{
		Name name=new Name(this);
		getAltNames().add(name);
		return name;
	}

	public void dropAltName(Name name)
	{
		if (altNames!=null) altNames.remove(name);
		name.delete();
	}

	public Set<Name> getAltNames()
	{
		if (altNames==null)
			altNames=DBLoader.getInstance().loadSet(Name.class, null, "type=? and ref_id=?", Name.MOVIE, getId());
		return altNames;
	}

	public String getIndexBy()
	{
		return indexBy;
	}

	public void setIndexBy(String indexBy)
	{
		this.indexBy=indexBy;
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public boolean isRecord()
	{
		return record;
	}

	public void setRecord(boolean record)
	{
		this.record=record;
		setModified();
	}

	public Picture getPoster()
	{
		return (Picture)getReference(POSTER);
	}

	public void setPoster(Picture picture)
	{
		setReference(POSTER, picture);
	}

	public String getJavaScript()
	{
		return javaScript;
	}

	public void setJavaScript(String javaScript)
	{
		this.javaScript=javaScript;
		setModified();
	}

	public String getWebScriptFile()
	{
		return webScriptFile;
	}

	public void setWebScriptFile(String webScriptFile)
	{
		this.webScriptFile=webScriptFile;
		setModified();
	}

	public boolean isUsed()
	{
		return super.isUsed() || MovieManager.getInstance().isMovieUsed(this);
	}

	public String toString()
	{
		return title;
	}

	public void afterReload()
	{
		altNames=null;
		super.afterReload();
	}

	public void setSummaryText(Language language, String text)
	{
		Summary summary=getSummary(language);
		if (!StringUtils.isEmpty(text))
		{
			if (summary==null)
			{
				summary=new Summary();
				summary.setMovie(this);
				summary.setLanguage(language);
			}
			summary.setSummary(text);
		}
		else
		{
			if (summary!=null) summary.delete();
		}
	}

	public String getSummaryText(Language language)
	{
		Summary summary=getSummary(language);
		return summary!=null ? summary.getSummary() : null;
	}

	private Summary getSummary(Language language)
	{
		return DBLoader.getInstance().load(Summary.class, null, "movie_id=? and language_id=?", getId(), language.getId());
	}

	public Integer getYear()
	{
		return year;
	}

	public void setYear(Integer year)
	{
		this.year=year;
		setModified();
	}

	public Integer getRuntime()
	{
		return runtime;
	}

	public void setRuntime(Integer runtime)
	{
		this.runtime=runtime;
		setModified();
	}

	public void addGenre(Genre genre)
	{
		ASSOCIATION_GENRES.addAssociation(this, genre);
	}

	public void removeGenre(Genre genre)
	{
		ASSOCIATION_GENRES.removeAssociation(this, genre);
	}

	public Set<Genre> getGenres()
	{
		return ASSOCIATION_GENRES.getAssociations(this);
	}

	public void setGenres(Collection<Genre> genres)
	{
		ASSOCIATION_GENRES.setAssociations(this, genres);
	}

	public void addLanguage(Language language)
	{
		ASSOCIATION_LANGUAGES.addAssociation(this, language);
	}

	public void removeLanguage(Language language)
	{
		ASSOCIATION_LANGUAGES.removeAssociation(this, language);
	}

	public Set<Language> getLanguages()
	{
		return ASSOCIATION_LANGUAGES.getAssociations(this);
	}

	public void setLanguages(Collection<Language> languages)
	{
		ASSOCIATION_LANGUAGES.setAssociations(this, languages);
	}

	public void addCountry(Country country)
	{
		ASSOCIATION_COUNTRIES.addAssociation(this, country);
	}

	public void removeCountry(Country country)
	{
		ASSOCIATION_COUNTRIES.removeAssociation(this, country);
	}

	public Set<Country> getCountries()
	{
		return ASSOCIATION_COUNTRIES.getAssociations(this);
	}

	public void setCountries(Collection<Country> countries)
	{
		ASSOCIATION_COUNTRIES.setAssociations(this, countries);
	}

	public Set<CastMember> getCastMembers()
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "movie_id=?", getId());
	}

	public Set<CrewMember> getCrewMembers()
	{
		return DBLoader.getInstance().loadSet(CrewMember.class, null, "movie_id=?", getId());
	}

	public int getRecordableLength()
	{
		return runtime!=null ? runtime : 0;
	}

	public String getRecordableName(Language language)
	{
		return getTitle(language);
	}

	public void initRecord(Recording recording)
	{
		recording.setMovie(this);
	}

	public Set<CrewMember> getCrewMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CrewMember.class, null, "movie_id=? and credit_type_id=?", getId(), type.getId());
	}

	public Set<CastMember> getCastMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "movie_id=? and credit_type_id=?", getId(), type.getId());
	}

	public boolean hasPoster()
	{
		return getReferenceId(POSTER)!=null;
	}
}
