/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.show;

import java.util.Set;
import java.util.Iterator;

import com.kiwisoft.utils.db.Chain;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.Language;

public class Episode extends IDObject implements Chain.ChainLink, Comparable
{
	public static final String SHOW="show";
	public static final String DEFAULT_INFO="defaultInfo";

	private String userKey;
	private String name;
	private String originalName;
	private int sequence;
	private boolean seen;
	private boolean record;
	private boolean good;
	private Set<Name> altNames;
	private Set<EpisodeInfo> infos;
	private String javaScript;
	private String webScriptFile;

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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
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

	public EpisodeInfo createInfo()
	{
		EpisodeInfo info=new EpisodeInfo(this);
		if (infos!=null) infos.add(info);
		return info;
	}

	public void dropInfo(EpisodeInfo info)
	{
		if (infos!=null) infos.remove(info);
		info.delete();
	}

	public Set getInfos()
	{
		if (infos==null)
			infos=DBLoader.getInstance().loadSet(EpisodeInfo.class, null, "episode_id=?", getId());
		return infos;
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public EpisodeInfo getDefaultInfo()
	{
		return (EpisodeInfo)getReference(DEFAULT_INFO);
	}

	public void setDefaultInfo(EpisodeInfo episodeInfo)
	{
		setReference(DEFAULT_INFO, episodeInfo);
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
		if (userKey!=null && name!=null) return userKey+": \""+name+"\"";
		else if (name!=null) return "\""+name+"\"";
		else return userKey;
	}

	public String getNameWithKey(Language language)
	{
		String name=null;
		if (language!=null)
		{
			if ("de".equals(language.getSymbol())) name=getName();
			if (getShow().getLanguage()==language) name=getOriginalName();
			else
			{
				for (Iterator it=getAltNames().iterator(); it.hasNext();)
				{
					Name altName=(Name)it.next();
					if (altName.getLanguage()==language)
					{
						name=altName.getName();
						break;
					}
				}
			}
		}
		else name=getName();
		if (userKey!=null && name!=null) return userKey+": \""+name+"\"";
		else if (name!=null) return "\""+name+"\"";
		else return userKey;
	}

	public String getName(Language language)
	{
		if (language!=null)
		{
			if ("de".equals(language.getSymbol())) return getName();
			if (getShow().getLanguage()==language) return getOriginalName();
			else
			{
				for (Iterator it=getAltNames().iterator(); it.hasNext();)
				{
					Name altName=(Name)it.next();
					if (altName.getLanguage()==language) return altName.getName();
				}
			}
		}
		return getName();
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

	public String getOriginalName()
	{
		return originalName;
	}

	public void setOriginalName(String originalName)
	{
		this.originalName=originalName;
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

	public String getLink()
	{
		EpisodeInfo link=getDefaultInfo();
		if (link!=null) return "/"+link.getPath()+"?episode="+getId();
		return null;
	}
}
