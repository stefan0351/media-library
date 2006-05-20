/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.movie;

import java.util.Set;
import java.util.Iterator;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.Identifyable;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.Name;

public class Movie extends IDObject
{
	public static final String SHOW="show";
	public static final String DEFAULT_INFO="defaultInfo";
	public static final String TYPE="type";

	private String name;
	private String originalName;
	private boolean seen;
	private boolean record;
	private boolean good;
	private Set<Name> altNames;
	private Set<MovieInfo> infos;
	private String javaScript;
	private String webScriptFile;

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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
		setModified();
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

	public String getName(Language language)
	{
		if (language!=null && "de".equals(language.getSymbol())) return getName();
		else
		{
			for (Iterator it=getAltNames().iterator(); it.hasNext();)
			{
				Name altName=(Name)it.next();
				if (altName.getLanguage()==language) return altName.getName();
			}
		}
		return getName();
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

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
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
		return super.isUsed() || MovieManager.getInstance().isMovieUsed(this);
	}

	public String toString()
	{
		return name;
	}

	public void afterReload()
	{
		altNames=null;
		super.afterReload();
	}

	public MovieInfo createInfo()
	{
		MovieInfo info=new MovieInfo(this);
		if (infos!=null) infos.add(info);
		return info;
	}

	public void dropInfo(MovieInfo info)
	{
		if (infos!=null) infos.remove(info);
		info.delete();
	}

	public Set getInfos()
	{
		if (infos==null)
			infos=DBLoader.getInstance().loadSet(MovieInfo.class, null, "movie_id=?", getId());
		return infos;
	}

	public MovieInfo getDefaultInfo()
	{
		return (MovieInfo)getReference(DEFAULT_INFO);
	}

	public void setDefaultInfo(MovieInfo movieInfo)
	{
		setReference(DEFAULT_INFO, movieInfo);
	}

	public String getLink()
	{
		MovieInfo link=getDefaultInfo();
		if (link!=null) return "/"+link.getPath()+"?movie="+getId();
		return null;
	}

	public MovieType getType()
	{
		return (MovieType)getReference(TYPE);
	}

	public void setType(MovieType type)
	{
		setReference(TYPE, type);
	}

	public Identifyable loadReference(String name, Long referenceId)
	{
		if (TYPE.equals(name)) return MovieType.get(referenceId);
		return super.loadReference(name, referenceId);
	}

}
