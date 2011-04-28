/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.show;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.media.files.MediaType;
import com.kiwisoft.media.medium.*;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.utils.StringUtils;

public class Episode extends IDObject implements ChainLink, Comparable, Production, Recordable
{
	public static final String SHOW="show";
	public static final String USER_KEY="userKey";
	public static final String PRODUCTION_CODE="productionCode";
	public static final String TITLE="title";
	public static final String AIRDATE="airdate";
	public static final String GERMAN_TITLE="germanTitle";
	
	private String userKey;
	private String title;
	private String germanTitle;
	private int sequence;
	private Set<Name> altNames;
	private String productionCode;
	private Date airdate;

	public Episode(Show show)
	{
		setShow(show);
	}

	public Episode(DBDummy dummy)
	{
		super(dummy);
	}

	public String getUserKey()
	{
		return userKey;
	}

	public void setUserKey(String userKey)
	{
		String oldKey=this.userKey;
		this.userKey=userKey;
		setModified(USER_KEY, oldKey, this.userKey);
	}

	public String getProductionCode()
	{
		return productionCode;
	}

	public void setProductionCode(String productionCode)
	{
		String oldCode=this.productionCode;
		this.productionCode=productionCode;
		setModified(PRODUCTION_CODE, oldCode, this.productionCode);
	}

	public Date getAirdate()
	{
		return airdate;
	}

	public void setAirdate(Date airdate)
	{
		Date oldDate=this.airdate;
		this.airdate=airdate;
		setModified(AIRDATE, oldDate, this.airdate);
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
			altNames=DBLoader.getInstance().loadSet(Name.class, null, "type=? and ref_id=?", Name.EPISODE, getId());
		return altNames;
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public Season getSeason()
	{
		return DBLoader.getInstance().load(Season.class,
										   "_join episodes ep1 on seasons.firstepisode_id=ep1.id left outer join episodes ep2 on seasons.lastepisode_id=ep2.id",
										   "seasons.show_id=? and ep1.sequence<=? and (ep2.sequence is null or ?<=ep2.sequence)",
										   getReferenceId(SHOW), getChainPosition(), getChainPosition());
	}

	@Override
	public void setChainPosition(int position)
	{
		int oldSequence=this.sequence;
		this.sequence=position;
		setModified("chainPosition", oldSequence, this.sequence);
	}

	@Override
	public int getChainPosition()
	{
		return sequence;
	}

	@Override
	public boolean isUsed()
	{
		return super.isUsed() || ShowManager.getInstance().isEpisodeUsed(this);
	}

	@Override
	public String toString()
	{
		if (userKey!=null && title!=null) return userKey+": \""+title+"\"";
		else if (title!=null) return "\""+title+"\"";
		else return userKey;
	}

	public String getTitleWithKey(Language language)
	{
		String title=getTitle(language);
		if (userKey!=null && title!=null) return userKey+": \""+title+"\"";
		else if (title!=null) return "\""+title+"\"";
		else return userKey;
	}

	public String getTitle(Language language)
	{
		if (language!=null)
		{
			if (getShow().getLanguage()==language) return getTitle();
			if ("de".equals(language.getSymbol()))
			{
				if (StringUtils.isEmpty(getGermanTitle())) return getTitle();
				return getGermanTitle();
			}
			for (Iterator it=getAltNames().iterator(); it.hasNext();)
			{
				Name altName=(Name)it.next();
				if (altName.getLanguage()==language) return altName.getName();
			}
		}
		return getTitle();
	}

	@Override
	public void afterReload()
	{
		altNames=null;
		super.afterReload();
	}

	@Override
	public int compareTo(Object o)
	{
		Episode episode=(Episode)o;
		if (sequence<episode.sequence) return -1;
		else if (sequence>episode.sequence) return 1;
		return 0;
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public void setGermanTitle(String germanTitle)
	{
		String oldTitle=this.germanTitle;
		this.germanTitle=germanTitle;
		setModified(GERMAN_TITLE, oldTitle, this.germanTitle);
	}

	public Episode getNextEpisode()
	{
		return getShow().getEpisodes().getNext(this);
	}

	public Episode getPreviousEpisode()
	{
		return getShow().getEpisodes().getPrevious(this);
	}

	public void setSummaryText(Language language, String text)
	{
		Summary summary=getSummary(language);
		if (!StringUtils.isEmpty(text))
		{
			if (summary==null)
			{
				summary=new Summary();
				summary.setEpisode(this);
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
		return DBLoader.getInstance().load(Summary.class, null, "episode_id=? and language_id=?", getId(), language.getId());
	}

	public Set<Summary> getSummaries()
	{
		return DBLoader.getInstance().loadSet(Summary.class, null, "episode_id=?", getId());
	}

	@Override
	public Set<Credit> getCredits()
	{
		return DBLoader.getInstance().loadSet(Credit.class, null, "episode_id=?", getId());
	}

	@Override
	public Set<Credit> getCredits(CreditType type)
	{
		return DBLoader.getInstance().loadSet(Credit.class, null, "episode_id=? and credit_type_id=?", getId(), type.getId());
	}

	@Override
	public Credit createCredit()
	{
		Credit credit=new Credit();
		credit.setEpisode(this);
		fireElementAdded(CREDITS, credit);
		return credit;
	}

	@Override
	public void dropCredit(Credit credit)
	{
		credit.delete();
		fireElementRemoved(CREDITS, credit);
	}

	@Override
	public String getProductionTitle()
	{
		Show show=getShow();
		return show.getTitle()+" - "+toString();
	}

	@Override
	public CreditType[] getSupportedCastTypes()
	{
		return new CreditType[]{CreditType.MAIN_CAST, CreditType.RECURRING_CAST, CreditType.GUEST_CAST};
	}

	@Override
	public Set<CastMember> getCastMembers()
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "episode_id=?", getId());
	}

	@Override
	public Set<CastMember> getCastMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "episode_id=? and credit_type_id=?", getId(), type.getId());
	}

	@Override
	public CastMember createCastMember(CreditType creditType)
	{
		CastMember cast=new CastMember();
		cast.setCreditType(creditType);
		cast.setEpisode(this);
		fireElementAdded(CAST_MEMBERS, cast);
		return cast;
	}

	@Override
	public void dropCastMember(CastMember cast)
	{
		cast.delete();
		fireElementRemoved(CAST_MEMBERS, cast);
	}

	@Override
	public int getRecordableLength()
	{
		return getShow().getDefaultEpisodeLength();
	}

	@Override
	public String getRecordableName(Language language)
	{
		Show show=getShow();
		return show.getTitle(language)+" - "+getTitleWithKey(language);
	}

	@Override
	public void initRecord(Track track)
	{
		track.setType(TrackType.VIDEO);
		track.setShow(getShow());
		track.setEpisode(this);
	}

	@Override
	public void delete()
	{
		for (Name name : new HashSet<Name>(getAltNames())) dropAltName(name);
		for (Summary summary : DBLoader.getInstance().loadSet(Summary.class, null, "episode_id=?", getId())) summary.delete();
		for (CastMember castMember : DBLoader.getInstance().loadSet(CastMember.class, null, "episode_id=?", getId())) castMember.delete();
		for (Credit crewMember : DBLoader.getInstance().loadSet(Credit.class, null, "episode_id=?", getId())) crewMember.delete();
		super.delete();
	}

	public boolean hasImages()
	{
		return MediaFileManager.getInstance().getNumberOfMediaFiles(this, MediaType.IMAGE)>0;
	}

	public boolean hasVideos()
	{
		return MediaFileManager.getInstance().getNumberOfMediaFiles(this, MediaType.VIDEO)>0;
	}

	public Set<Medium> getMedia()
	{
		return MediumManager.getInstance().getMedia(this);
	}
}
