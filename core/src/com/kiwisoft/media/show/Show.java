/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.show;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import com.kiwisoft.media.*;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.dataImport.SearchManager;
import com.kiwisoft.media.dataImport.SearchPattern;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.FanFicGroup;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;

public class Show extends IDObject implements FanFicGroup, Linkable, Production
{
	public static final String AIRDATES="airdates";
	public static final String EPISODES="episodes";
	public static final String SEASONS="seasons";
	public static final String MOVIES="movies";
	public static final String MAIN_CAST="mainCast";
	public static final String RECURRING_CAST="recurringCast";
	public static final String LANGUAGE="language";
	public static final String DEFAULT_INFO="defaultInfo";
	public static final String GENRES="genres";
	public static final String LOGO="logo";
	public static final String TITLE="title";

	private String userKey;
	private String title;
	private String germanTitle;
	private Set<Name> altNames;
	private boolean internet;
	private int defaultEpisodeLength;
	private Chain<Episode> episodes;
	private Set<Movie> movies;
	private Set<Season> seasons;
	private String webDatesFile;
	private Set<ShowInfo> infos;
	private Integer startYear, endYear;
	private String indexBy;

	public Show()
	{
	}

	public Show(DBDummy dummy)
	{
		super(dummy);
	}

	public String getFanFicGroupName()
	{
		return getTitle();
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		String oldTitle=this.title;
		this.title=title;
		setModified(TITLE, oldTitle, this.title);
	}

	public String getTitle(Language language)
	{
		if (language!=null)
		{
			if (getLanguage()==language) return getTitle();
			if ("de".equals(language.getSymbol()))
			{
				String title=getGermanTitle();
				return StringUtils.isEmpty(title) ? getTitle() : title;
			}
			for (Name name1 : getAltNames())
			{
				if (name1.getLanguage()==language) return name1.getName();
			}
		}
		return getTitle();
	}

	public Name createAltName()
	{
		Name showName=new Name(this);
		getAltNames().add(showName);
		return showName;
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

	public ShowInfo createInfo()
	{
		ShowInfo info=new ShowInfo(this);
		if (infos!=null) infos.add(info);
		return info;
	}

	public void dropInfo(ShowInfo info)
	{
		if (infos!=null) infos.remove(info);
		info.delete();
	}

	public Set<ShowInfo> getInfos()
	{
		if (infos==null) infos=DBLoader.getInstance().loadSet(ShowInfo.class, null, "show_id=?", getId());
		return infos;
	}

	public Episode createEpisode()
	{
		Episode episode=new Episode(this);
		getEpisodes().addNew(episode);
		fireElementAdded(EPISODES, episode);
		return episode;
	}

	public void dropEpisode(Episode episode)
	{
		getEpisodes().remove(episode);
		episode.delete();
		fireElementRemoved(EPISODES, episode);
	}

	public Chain<Episode> getEpisodes()
	{
		if (episodes==null)
		{
			episodes=new Chain<Episode>(DBLoader.getInstance().loadSet(Episode.class, null, "show_id=?", getId()));
		}
		return episodes;
	}

	public Set<Track> getRecordings()
	{
		return DBLoader.getInstance().loadSet(Track.class, null, "show_id=?", getId());
	}

	public int getRecordingCount()
	{
		return DBLoader.getInstance().count(Track.class, null, "show_id=?", getId());
	}

	public Movie createMovie()
	{
		Movie movie=MovieManager.getInstance().createMovie(this);
		if (movies!=null) movies.add(movie);
		fireElementAdded(MOVIES, movie);
		return movie;
	}

	public void dropMovie(Movie movie)
	{
		if (movies!=null) movies.remove(movie);
		MovieManager.getInstance().dropMovie(movie);
		fireElementRemoved(MOVIES, movie);
	}

	public Set<Movie> getMovies()
	{
		if (movies==null) movies=DBLoader.getInstance().loadSet(Movie.class, null, "show_id=?", getId());
		return movies;
	}

	public Set<Season> getSeasons()
	{
		if (seasons==null)
			seasons=DBLoader.getInstance().loadSet(Season.class, null, "show_id=?", getId());
		return seasons;
	}

	public Season createSeason()
	{
		Season season=new Season(this);
		if (seasons!=null) seasons.add(season);
		fireElementAdded(SEASONS, season);
		return season;
	}

	public void dropSeason(Season season)
	{
		if (seasons!=null) seasons.remove(season);
		season.delete();
		fireElementRemoved(SEASONS, season);
	}

	public Collection<Airdate> getAirdates()
	{
		return DBLoader.getInstance().loadSet(Airdate.class, null, "show_id=?", getId());
	}

	public String getUserKey()
	{
		return userKey;
	}

	public void setUserKey(String userKey)
	{
		this.userKey=userKey;
		setModified();
	}

	public boolean isInternet()
	{
		return internet;
	}

	public void setInternet(boolean internet)
	{
		this.internet=internet;
		setModified();
	}

	public int getDefaultEpisodeLength()
	{
		return defaultEpisodeLength;
	}

	public void setDefaultEpisodeLength(int defaultEpisodeLength)
	{
		this.defaultEpisodeLength=defaultEpisodeLength;
		setModified();
	}

	public String getWebDatesFile()
	{
		return webDatesFile;
	}

	public void setWebDatesFile(String webDatesFile)
	{
		this.webDatesFile=webDatesFile;
		setModified();
	}

	public boolean isUsed()
	{
		return super.isUsed() || ShowManager.getInstance().isShowUsed(this);
	}

	public void delete()
	{
		for (Episode episode : new HashSet<Episode>(getEpisodes().elements())) dropEpisode(episode);
		super.delete();
	}

	public String toString()
	{
		return getTitle();
	}

	public void afterReload()
	{
		altNames=null;
		episodes=null;
		movies=null;
		seasons=null;
		super.afterReload();
	}

	public String getSearchPattern(int type)
	{
		SearchPattern pattern=SearchManager.getInstance().getSearchPattern(type, this);
		if (pattern!=null)
			return pattern.getPattern();
		else
			return null;
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

	public Set<CastMember> getMainCast()
	{
		return getCastMembers(CreditType.MAIN_CAST);
	}

	public CastMember createMainCast()
	{
		CastMember cast=new CastMember();
		cast.setCreditType(CreditType.MAIN_CAST);
		cast.setShow(this);
		fireElementAdded(MAIN_CAST, cast);
		return cast;
	}

	public Set<CastMember> getRecurringCast()
	{
		return getCastMembers(CreditType.RECURRING_CAST);
	}

	public CastMember createRecurringCast()
	{
		CastMember cast=new CastMember();
		cast.setCreditType(CreditType.RECURRING_CAST);
		cast.setShow(this);
		fireElementAdded(RECURRING_CAST, cast);
		return cast;
	}

	public void dropCast(CastMember cast)
	{
		cast.delete();
		if (cast.getCreditType()==CreditType.MAIN_CAST)
			fireElementRemoved(MAIN_CAST, cast);
		else if (cast.getCreditType()==CreditType.RECURRING_CAST)
			fireElementRemoved(RECURRING_CAST, cast);

	}

	public Set getFanDoms()
	{
		return DBLoader.getInstance().loadSet(FanDom.class, null, "show_id=?", getId());
	}

	public Set<FanFic> getFanFics()
	{
		return DBLoader.getInstance().loadSet(FanFic.class, "map_fanfic_fandom map, fandoms",
											  "map.fanfic_id=fanfics.id and map.fandom_id=fandoms.id and fandoms.show_id=?", getId());
	}

	public int getFanFicCount()
	{
		return DBLoader.getInstance().count(FanFic.class, "map_fanfic_fandom map, fandoms",
											"map.fanfic_id=fanfics.id and map.fandom_id=fandoms.id and fandoms.show_id=?", getId());
	}

	public boolean contains(FanFic fanFic)
	{
		throw new UnsupportedOperationException();
	}

	public SortedSet<Character> getFanFicLetters()
	{
		return FanFicManager.getInstance().getFanFicLetters(this);
	}

	public Set<FanFic> getFanFics(char ch)
	{
		return FanFicManager.getInstance().getFanFics(this, ch);
	}

	public String getHttpParameter()
	{
		return "show="+getId();
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language value)
	{
		setReference(LANGUAGE, value);
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public void setGermanTitle(String title)
	{
		this.germanTitle=title;
		setModified();
	}

	public Picture getLogo()
	{
		return (Picture)getReference(LOGO);
	}

	public void setLogo(Picture logo)
	{
		setReference(LOGO, logo);
	}

	public ShowInfo getDefaultInfo()
	{
		return (ShowInfo)getReference(DEFAULT_INFO);
	}

	public void setDefaultInfo(ShowInfo info)
	{
		setReference(DEFAULT_INFO, info);
	}

	public void addGenre(Genre genre)
	{
		createAssociation(GENRES, genre);
	}

	public void removeGenre(Genre genre)
	{
		dropAssociation(GENRES, genre);
	}

	@SuppressWarnings({"unchecked"})
	public Set<Genre> getGenres()
	{
		return getAssociations(GENRES);
	}

	public void setGenres(Collection<Genre> genres)
	{
		setAssociations(GENRES, genres);
	}

	public Set<Credit> getCredits(CreditType type)
	{
		return DBLoader.getInstance().loadSet(Credit.class, null, "show_id=? and credit_type_id=?", getId(), type.getId());
	}

	public Set<CastMember> getCastMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "show_id=? and credit_type_id=?", getId(), type.getId());
	}

	public Integer getStartYear()
	{
		return startYear;
	}

	public void setStartYear(Integer startYear)
	{
		this.startYear=startYear;
		setModified();
	}

	public Integer getEndYear()
	{
		return endYear;
	}

	public void setEndYear(Integer endYear)
	{
		this.endYear=endYear;
		setModified();
	}

	public String getYearString()
	{
		if (startYear==null) return null;
		else if (endYear==null) return startYear+"-";
		else if (endYear==startYear) return String.valueOf(startYear);
		else return startYear+"-"+endYear;
	}


	public String getIndexBy()
	{
		return indexBy;
	}

	public void setIndexBy(String indexBy)
	{
		this.indexBy=indexBy;
		setModified();
	}

	public LinkGroup getLinkGroup()
	{
		return (LinkGroup)getReference(LINK_GROUP);
	}

	public LinkGroup getLinkGroup(boolean create)
	{
		LinkGroup group=getLinkGroup();
		if (group==null && create) setLinkGroup(group=LinkManager.getInstance().createRootGroup(getTitle()));
		return group;
	}

	public void setLinkGroup(LinkGroup group)
	{
		setReference(LINK_GROUP, group);
	}
}
