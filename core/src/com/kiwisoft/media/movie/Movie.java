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
import java.util.HashSet;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Summary;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.*;
import com.kiwisoft.media.dataimport.SearchManager;
import com.kiwisoft.media.dataimport.SearchPattern;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.medium.Recordable;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.medium.TrackType;
import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBAssociation;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBLoader;

public class Movie extends IDObject implements Recordable, Production
{
	public static final String SHOW="show";
	public static final String DEFAULT_INFO="defaultInfo";
	public static final String GENRES="genres";
	public static final String LANGUAGES="languages";
	public static final String COUNTRIES="countries";
	public static final String INDEX_BY="indexBy";
	public static final String POSTER="poster";

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
		String oldImdbKey=this.imdbKey;
		this.imdbKey=imdbKey;
		setModified("imdbKey", oldImdbKey, this.imdbKey);
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		String oldTitle=this.title;
		this.title=title;
		setModified("title", oldTitle, this.title);
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public void setGermanTitle(String germanTitle)
	{
		String oldTitle=this.germanTitle;
		this.germanTitle=germanTitle;
		setModified("germanTitle", oldTitle, this.germanTitle);
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
		String oldIndexBy=this.indexBy;
		this.indexBy=indexBy;
		setModified("indexBy", oldIndexBy, this.indexBy);
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
		boolean oldRecord=this.record;
		this.record=record;
		setModified("record", oldRecord, this.record);
	}

	public MediaFile getPoster()
	{
		return (MediaFile)getReference(POSTER);
	}

	public void setPoster(MediaFile picture)
	{
		setReference(POSTER, picture);
	}

	public String getJavaScript()
	{
		return javaScript;
	}

	public void setJavaScript(String javaScript)
	{
		String oldJavaScript=this.javaScript;
		this.javaScript=javaScript;
		setModified("javaScript", oldJavaScript, this.javaScript);
	}

	public String getWebScriptFile()
	{
		return webScriptFile;
	}

	public void setWebScriptFile(String webScriptFile)
	{
		String oldValue=this.webScriptFile;
		this.webScriptFile=webScriptFile;
		setModified("webScriptFile", oldValue, this.webScriptFile);
	}

	public boolean isUsed()
	{
		if (!super.isUsed())
		{
			DBLoader dbLoader=DBLoader.getInstance();
			if (dbLoader.count(FanDom.class, null, "movie_id=?", getId())>0) return true;
			if (dbLoader.count(Airdate.class, null, "movie_id=?", getId())>0) return true;
			if (dbLoader.count(Track.class, null, "movie_id=?", getId())>0) return true;
			return false;
		}
		return true;
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
		Integer oldYear=this.year;
		this.year=year;
		setModified("year", oldYear, this.year);
	}

	public Integer getRuntime()
	{
		return runtime;
	}

	public void setRuntime(Integer runtime)
	{
		Integer oldRuntime=this.runtime;
		this.runtime=runtime;
		setModified("runtime", oldRuntime, this.runtime);
	}

	public void addGenre(Genre genre)
	{
		DBAssociation.getAssociation(Movie.class, GENRES).addAssociation(this, genre);
	}

	public void removeGenre(Genre genre)
	{
		DBAssociation.getAssociation(Movie.class, GENRES).removeAssociation(this, genre);
	}

	@SuppressWarnings({"unchecked"})
	public Set<Genre> getGenres()
	{
		return (Set<Genre>)DBAssociation.getAssociation(Movie.class, GENRES).getAssociations(this);
	}

	public void setGenres(Collection<Genre> genres)
	{
		DBAssociation.getAssociation(Movie.class, GENRES).setAssociations(this, genres);
	}

	public void addLanguage(Language language)
	{
		DBAssociation.getAssociation(Movie.class, LANGUAGES).addAssociation(this, language);
	}

	public void removeLanguage(Language language)
	{
		DBAssociation.getAssociation(Movie.class, LANGUAGES).removeAssociation(this, language);
	}

	@SuppressWarnings({"unchecked"})
	public Set<Language> getLanguages()
	{
		return (Set<Language>)DBAssociation.getAssociation(Movie.class, LANGUAGES).getAssociations(this);
	}

	public void setLanguages(Collection<Language> languages)
	{
		DBAssociation.getAssociation(Movie.class, LANGUAGES).setAssociations(this, languages);
	}

	public void addCountry(Country country)
	{
		DBAssociation.getAssociation(Movie.class, COUNTRIES).addAssociation(this, country);
	}

	public void removeCountry(Country country)
	{
		DBAssociation.getAssociation(Movie.class, COUNTRIES).removeAssociation(this, country);
	}

	@SuppressWarnings({"unchecked"})
	public Set<Country> getCountries()
	{
		return (Set<Country>)DBAssociation.getAssociation(Movie.class, COUNTRIES).getAssociations(this);
	}

	public void setCountries(Collection<Country> countries)
	{
		DBAssociation.getAssociation(Movie.class, COUNTRIES).setAssociations(this, countries);
	}

	public String getProductionTitle()
	{
		return getTitle();
	}

	public CreditType[] getSupportedCastTypes()
	{
		return new CreditType[]{CreditType.MAIN_CAST};
	}

	public Set<CastMember> getCastMembers()
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "movie_id=?", getId());
	}

	public Set<CastMember> getCastMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "movie_id=? and credit_type_id=?", getId(), type.getId());
	}

	public CastMember createCastMember(CreditType creditType)
	{
		CastMember cast=new CastMember();
		cast.setCreditType(creditType);
		cast.setMovie(this);
		fireElementAdded(CAST_MEMBERS, cast);
		return cast;
	}

	public void dropCastMember(CastMember cast)
	{
		cast.delete();
		fireElementRemoved(CAST_MEMBERS, cast);
	}

	public int getRecordableLength()
	{
		return runtime!=null ? runtime : 0;
	}

	public String getRecordableName(Language language)
	{
		return getTitle(language);
	}

	public void initRecord(Track track)
	{
		track.setType(TrackType.VIDEO);
		track.setMovie(this);
	}

	public Set<Credit> getCredits()
	{
		return DBLoader.getInstance().loadSet(Credit.class, null, "movie_id=?", getId());
	}

	public Set<Credit> getCredits(CreditType type)
	{
		return DBLoader.getInstance().loadSet(Credit.class, null, "movie_id=? and credit_type_id=?", getId(), type.getId());
	}

	public Credit createCredit()
	{
		Credit credit=new Credit();
		credit.setMovie(this);
		fireElementAdded(CREDITS, credit);
		return credit;
	}

	public void dropCredit(Credit credit)
	{
		credit.delete();
		fireElementRemoved(CREDITS, credit);
	}

	public boolean hasPoster()
	{
		return getReferenceId(POSTER)!=null;
	}

	@Override
	public void delete()
	{
		for (Credit credit : new HashSet<Credit>(getCredits())) dropCredit(credit);
		for (CastMember credit : new HashSet<CastMember>(getCastMembers())) dropCastMember(credit);
		for (SearchPattern pattern : SearchManager.getInstance().getSearchPattern(this)) pattern.delete();
		for (Summary summary : DBLoader.getInstance().loadSet(Summary.class, null, "movie_id=?", getId())) summary.delete();
		super.delete();
	}
}
