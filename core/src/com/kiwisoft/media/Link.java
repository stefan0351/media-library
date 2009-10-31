/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media;

import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;

public class Link extends IDObject implements Comparable
{
	public static final String NAME="name";
	public static final String GROUP="group";
	public static final String LANGUAGE="language";

	private String name;
	private String url;

	public Link(LinkGroup group)
	{
		setGroup(group);
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
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, this.name);
	}

	public void setUrl(String url)
	{
		String oldUrl=this.url;
		this.url=url;
		setModified("url", oldUrl, this.url);
	}

	public String getUrl()
	{
		return url;
	}

	public LinkGroup getGroup()
	{
		return (LinkGroup)getReference(GROUP);
	}

	public void setGroup(LinkGroup group)
	{
		setReference(GROUP, group);
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	@Override
	public int compareTo(Object o)
	{
		Link link2=(Link)o;
		return name.compareToIgnoreCase(link2.name);
	}

	public void setName(Object linkName)
	{
	}
}
