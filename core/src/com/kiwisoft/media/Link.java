/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.fanfic.FanDom;

public class Link extends IDObject implements Comparable
{
	public static final String SHOW="show";
	public static final String FANDOM="fanDom";
	public static final String LANGUAGE="language";

	private String name;
	private String url;

	public Link(Show show)
	{
		setShow(show);
	}

	public Link(FanDom fanDom)
	{
		setFanDom(fanDom);
	}

	public Link(DBDummy dummy)
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

	public void setUrl(String url)
	{
		this.url=url;
		setModified();
}

	public String getUrl()
	{
		return url;
   }

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public FanDom getFanDom()
	{
		return (FanDom)getReference(FANDOM);
	}

	public void setFanDom(FanDom fanDom)
	{
		setReference(FANDOM, fanDom);
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	public int compareTo(Object o)
	{
		Link link2=(Link)o;
		return name.compareToIgnoreCase(link2.name);
	}
}
