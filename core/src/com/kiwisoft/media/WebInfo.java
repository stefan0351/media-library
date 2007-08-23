package com.kiwisoft.media;

import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;

/**
 * @author Stefan Stiller
 */
public abstract class WebInfo extends IDObject
{
	public static final String LANGUAGE="language";

	private String name;
	private String path;

	protected WebInfo()
	{
	}

	protected WebInfo(DBDummy dummy)
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

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path=path;
		setModified();
	}

	public abstract boolean isDefault();

	public String toString()
	{
		return name;
	}
}
