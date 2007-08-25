/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.IDObject;

public class Channel extends IDObject
{
	public static final String LANGUAGE="language";

	private String name;
	private String logo;
	private boolean receivable;
	private Set<Name> altNames;

	public Channel()
	{
	}

	public Channel(DBDummy dummy)
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
			altNames=DBLoader.getInstance().loadSet(Name.class, null, "ref_id=?", getId());
		return altNames;
	}

	public String getLogo()
	{
		return logo;
	}

	public void setLogo(String logo)
	{
		this.logo=logo;
		setModified();
	}

	public boolean isReceivable()
	{
		return receivable;
	}

	public void setReceivable(boolean receivable)
	{
		this.receivable=receivable;
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

	public String toString()
	{
		if (name!=null) return name;
		return super.toString();
	}

	public void afterReload()
	{
		altNames=null;
		super.afterReload();
	}

	public boolean isUsed()
	{
		return super.isUsed() || ChannelManager.getInstance().isChannelUsed(this);
	}
}
