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

import com.kiwisoft.media.Language;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CrewMember;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.video.Recordable;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.Chain;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.IDObject;

public class Episode extends IDObject implements Chain.ChainLink, Comparable, Production, Recordable
{
	public static final String SHOW="show";

	private String userKey;
	private String title;
	private String germanTitle;
	private int sequence;
	private boolean seen;
	private boolean record;
	private boolean good;
	private Set<Name> altNames;
	private String javaScript;
	private String webScriptFile;
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
		this.userKey=userKey;
		setModified();
	}

	public String getProductionCode()
	{
		return productionCode;
	}

	public void setProductionCode(String productionCode)
	{
		this.productionCode=productionCode;
		setModified();
	}

	public Date getAirdate()
	{
		return airdate;
	}

	public void setAirdate(Date airdate)
	{
		this.airdate=airdate;
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

	public void setChainPosition(int position)
	{
		this.sequence=position;
		setModified();
	}

	public int getChainPosition()
	{
		return sequence;
	}

	public boolean isSeen()
	{
		return seen;
	}

	public void setSeen(boolean seen)
	{
		this.seen=seen;
		setModified();
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

	public boolean isGood()
	{
		return good;
	}

	public void setGood(boolean good)
	{
		this.good=good;
		setModified();
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
		return super.isUsed() || ShowManager.getInstance().isEpisodeUsed(this);
	}

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

	public void afterReload()
	{
		altNames=null;
		super.afterReload();
	}

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
		this.germanTitle=germanTitle;
		setModified();
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

	public Set<CrewMember> getCrewMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CrewMember.class, null, "episode_id=? and credit_type_id=?", getId(), type.getId());
	}

	public Set<CastMember> getCastMembers(CreditType type)
	{
		return DBLoader.getInstance().loadSet(CastMember.class, null, "episode_id=? and credit_type_id=?", getId(), type.getId());
	}

	public int getRecordableLength()
	{
		return getShow().getDefaultEpisodeLength();
	}

	public String getRecordableName(Language language)
	{
		Show show=getShow();
		return show.getTitle(language)+" - "+getTitleWithKey(language);
	}

	public void initRecord(Recording recording)
	{
		recording.setShow(getShow());
		recording.setEpisode(this);
	}

	public void delete()
	{
		for (Name name : new HashSet<Name>(getAltNames())) dropAltName(name);
		for (Summary summary : DBLoader.getInstance().loadSet(Summary.class, null, "episode_id=?", getId())) summary.delete();
		for (CastMember castMember : DBLoader.getInstance().loadSet(CastMember.class, null, "episode_id=?", getId())) castMember.delete();
		for (CrewMember crewMember : DBLoader.getInstance().loadSet(CrewMember.class, null, "episode_id=?", getId())) crewMember.delete();
		super.delete();
	}

}
