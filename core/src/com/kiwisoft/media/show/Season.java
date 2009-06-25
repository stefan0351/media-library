/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.show;

import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.media.files.MediaFile;

public class Season extends IDObject implements Comparable
{
	public static final String SHOW="show";
	public static final String FIRST_EPISODE="firstEpisode";
	public static final String LAST_EPISODE="lastEpisode";
	public static final String LOGO="logo";

	public static final ResourceBundle NAME_RESOURCES=ResourceBundle.getBundle("com.kiwisoft.media.show.seasons");

	private int number;
	private String name;
	private int startYear;
	private int endYear;

	public Season(Show show)
	{
		setShow(show);
	}

	public Season(DBDummy dummy)
	{
		super(dummy);
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		int oldNumber=this.number;
		this.number=number;
		setModified("number", oldNumber, this.number);
	}

	public int getStartYear()
	{
		return startYear;
	}

	public void setStartYear(int startYear)
	{
		int oldYear=this.startYear;
		this.startYear=startYear;
		setModified("startYear", oldYear, this.startYear);
	}

	public int getEndYear()
	{
		return endYear;
	}

	public void setEndYear(int endYear)
	{
		int oldYear=this.endYear;
		this.endYear=endYear;
		setModified("endYear", oldYear, this.endYear);
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public Long getShowId()
	{
		return (Long)getReferenceId(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public Episode getFirstEpisode()
	{
		return (Episode)getReference(FIRST_EPISODE);
	}

	public void setFirstEpisode(Episode value)
	{
		setReference(FIRST_EPISODE, value);
	}

	public Episode getLastEpisode()
	{
		return (Episode)getReference(LAST_EPISODE);
	}

	public void setLastEpisode(Episode value)
	{
		setReference(LAST_EPISODE, value);
	}

	public MediaFile getLogo()
	{
		return (MediaFile)getReference(LOGO);
	}

	public void setLogo(MediaFile logo)
	{
		setReference(LOGO, logo);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		setModified("name", oldName, this.name);
	}

	public String getSeasonName()
	{
		if (StringUtils.isEmpty(name)) return getName(number);
		else return name;
	}

	public static String getName(int number)
	{
		try
		{
			return NAME_RESOURCES.getString(String.valueOf(number));
		}
		catch (Exception e)
		{
			return number+". Staffel";
		}
	}

	public String getYearString()
	{
		if (startYear==0) return null;
		else if (endYear==0) return startYear+"-";
		else if (endYear==startYear) return String.valueOf(startYear);
		else return startYear+"-"+endYear;
	}

	public Set getEpisodes()
	{
		Episode firstEpisode=getFirstEpisode();
		if (firstEpisode==null) return Collections.EMPTY_SET;
		else
		{
			Episode lastEpisode=getLastEpisode();
			if (lastEpisode!=null)
				return DBLoader.getInstance().loadSet(Episode.class, null, "show_id=? and sequence>=? and sequence<=?",
						getShow().getId(), firstEpisode.getChainPosition(), lastEpisode.getChainPosition());
			else
				return DBLoader.getInstance().loadSet(Episode.class, null, "show_id=? and sequence>=?",
						getShow().getId(), firstEpisode.getChainPosition());
		}
	}

	@Override
	public String toString()
	{
		String yearString=getYearString();
		if (yearString!=null) return getSeasonName()+" ("+yearString+")";
		else return getSeasonName();
	}

	@Override
	public void afterReload()
	{
		super.afterReload();
	}

	public int compareTo(Object o)
	{
		Season season=(Season)o;
		if (getNumber()<season.getNumber()) return -1;
		else if (getNumber()>season.getNumber()) return 1;
		return 0;
	}
}
