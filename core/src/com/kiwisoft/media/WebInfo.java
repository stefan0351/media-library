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
		String oldName=this.name;
		this.name=name;
		setModified("name", oldName, this.name);
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
		String oldPath=this.path;
		this.path=path;
		setModified("path", oldPath, this.path);
	}

	public abstract boolean isDefault();

	@Override
	public String toString()
	{
		return name;
	}
}
