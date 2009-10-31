/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.show;

import java.util.*;

import com.kiwisoft.media.*;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.media.files.MediaType;
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
	public static final String LANGUAGE="language";
	public static final String DEFAULT_INFO="defaultInfo";
	public static final String GENRES="genres";
	public static final String LOGO="logo";
	public static final String TITLE="title";
	public static final String MEDIA_FILES="mediaFiles";
	public static final String BOOKS="books";

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
	private Set<Book> books;

	public Show()
	{
	}

	public Show(DBDummy dummy)
	{
		super(dummy);
	}

	@Override
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
		String oldKey=this.userKey;
		this.userKey=userKey;
		setModified("userKey", oldKey, this.userKey);
	}

	public boolean isInternet()
	{
		return internet;
	}

	public void setInternet(boolean internet)
	{
		boolean oldInternet=this.internet;
		this.internet=internet;
		setModified("internet", oldInternet, this.internet);
	}

	public int getDefaultEpisodeLength()
	{
		return defaultEpisodeLength;
	}

	public void setDefaultEpisodeLength(int defaultEpisodeLength)
	{
		int oldLength=this.defaultEpisodeLength;
		this.defaultEpisodeLength=defaultEpisodeLength;
		setModified("defaultEpisodeLength", oldLength, this.defaultEpisodeLength);
	}

	public String getWebDatesFile()
	{
		return webDatesFile;
	}

	public void setWebDatesFile(String webDatesFile)
	{
		String oldFile=this.webDatesFile;
		this.webDatesFile=webDatesFile;
		setModified("webDatesFile", oldFile, this.webDatesFile);
	}

	@Override
	public boolean isUsed()
	{
		return super.isUsed() || ShowManager.getInstance().isShowUsed(this);
	}

	@Override
	public void delete()
	{
		for (Episode episode : new HashSet<Episode>(getEpisodes().elements())) dropEpisode(episode);
		super.delete();
	}

	@Override
	public String toString()
	{
		return getTitle();
	}

	@Override
	public void afterReload()
	{
		altNames=null;
		episodes=null;
		movies=null;
		seasons=null;
		super.afterReload();
	}

	public Set<CastMember> getMainCast()
	{
		return getCastMembers(CreditType.MAIN_CAST);
	}

	public Set getFanDoms()
	{
		return DBLoader.getInstance().loadSet(FanDom.class, null, "show_id=?", getId());
	}

	@Override
	public Set<FanFic> getFanFics()
	{
		return DBLoader.getInstance().loadSet(FanFic.class, "map_fanfic_fandom map, fandoms",
											  "map.fanfic_id=fanfics.id and map.fandom_id=fandoms.id and fandoms.show_id=?", getId());
	}

	@Override
	public int getFanFicCount()
	{
		return DBLoader.getInstance().count(FanFic.class, "map_fanfic_fandom map, fandoms",
											"map.fanfic_id=fanfics.id and map.fandom_id=fandoms.id and fandoms.show_id=?", getId());
	}

	@Override
	public boolean contains(FanFic fanFic)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<Character> getFanFicLetters()
	{
		return FanFicManager.getInstance().getFanFicLetters(this);
	}

	@Override
	public Set<FanFic> getFanFics(char ch)
	{
		return FanFicManager.getInstance().getFanFics(this, ch);
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
		String oldTitle=this.germanTitle;
		this.germanTitle=title;
		setModified("germanTitle", oldTitle, this.germanTitle);
	}

	public MediaFile getLogo()
	{
		return (MediaFile)getReference(LOGO);
	}

	public void setLogo(MediaFile logo)
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

	@Override
	public Set<Credit> getCredits()
	{
		return Collections.emptySet();
	}

	@Override
	public Set<Credit> getCredits(CreditType type)
	{
		return Collections.emptySet();
	}

	@Override
	public Credit createCredit()
	{
		return null;
	}

	@Override
	public void dropCredit(Credit credit)
	{
	}

	@Override
	public String getProductionTitle()
	{
		return getTitle();
	}

	@Override
	public CreditType[] getSupportedCastTypes()
	{
		return new CreditType[]{CreditType.MAIN_CAST, CreditType.RECURRING_CAST};
	}

	@Override
	public Set<CastMember> getCastMembers()
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "show_id=?", getId());
	}

	@Override
	public Set<CastMember> getCastMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "show_id=? and credit_type_id=?", getId(), type.getId());
	}

	@Override
	public CastMember createCastMember(CreditType creditType)
	{
		CastMember cast=new CastMember();
		cast.setCreditType(creditType);
		cast.setShow(this);
		fireElementAdded(CAST_MEMBERS, cast);
		return cast;
	}

	@Override
	public void dropCastMember(CastMember cast)
	{
		cast.delete();
		fireElementRemoved(CAST_MEMBERS, cast);
	}

	public Integer getStartYear()
	{
		return startYear;
	}

	public void setStartYear(Integer startYear)
	{
		Integer oldYear=this.startYear;
		this.startYear=startYear;
		setModified("startYear", oldYear, this.startYear);
	}

	public Integer getEndYear()
	{
		return endYear;
	}

	public void setEndYear(Integer endYear)
	{
		Integer oldYear=this.endYear;
		this.endYear=endYear;
		setModified("endYear", oldYear, this.endYear);
	}

	public String getYearString()
	{
		if (startYear==null) return null;
		else if (endYear==null) return startYear+"-";
		else if (endYear.equals(startYear)) return String.valueOf(startYear);
		else return startYear+"-"+endYear;
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

	public LinkGroup getLinkGroup()
	{
		return (LinkGroup)getReference(LINK_GROUP);
	}

	@Override
	public LinkGroup getLinkGroup(boolean create)
	{
		LinkGroup group=getLinkGroup();
		if (group==null && create)
		{
			LinkGroup showsGroup=LinkManager.getInstance().getRootGroup("TV Shows");
			if (showsGroup!=null) group=showsGroup.createSubGroup(getTitle());
			else group=LinkManager.getInstance().createRootGroup(getTitle());
			setLinkGroup(group);
		}
		return group;
	}

	public void setLinkGroup(LinkGroup group)
	{
		setReference(LINK_GROUP, group);
	}

	public boolean hasImages()
	{
		return MediaFileManager.getInstance().getNumberOfMediaFiles(this, MediaType.IMAGE)>0;
	}

	public boolean hasVideos()
	{
		return MediaFileManager.getInstance().getNumberOfMediaFiles(this, MediaType.VIDEO)>0;
	}

	public Set<Book> getBooks()
	{
		if (books==null) books=DBLoader.getInstance().loadSet(Book.class, null, "show_id=?", getId());
		return books;
	}

	public void addBook(Book book)
	{
		boolean changed=getBooks().add(book);
		if (changed)
		{
			if (book.getShow()!=this) book.setShow(this);
			fireElementAdded(BOOKS, book);
		}
	}

	public void removeBook(Book book)
	{
		boolean changed=getBooks().remove(book);
		if (changed)
		{
			if (book.getShow()==this) book.setShow(null);
			fireElementRemoved(BOOKS, book);
		}
	}
}
