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
import com.kiwisoft.media.files.MediaFile;

public class Channel extends IDObject
{
	public static final String LANGUAGE="language";
	public static final String LOGO="logo";
	public static final String WEB_ADDRESS="webAddress";
	public static final String NAME="name";
	public static final String RECEIVABLE="receivable";
	public static final String TVTV_KEY="tvtvKey";

	private String name;
	private String webAddress;
	private Set<Name> altNames;
	private String tvtvKey;

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
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, this.name);
	}

	public String getTvtvKey()
	{
		return tvtvKey;
	}

	public void setTvtvKey(String tvtvKey)
	{
		String oldKey=this.tvtvKey;
		this.tvtvKey=tvtvKey;
		setModified(TVTV_KEY, oldKey, this.tvtvKey);
	}

	public String getWebAddress()
	{
		return webAddress;
	}

	public void setWebAddress(String webAddress)
	{
		String oldWebAddress=this.webAddress;
		this.webAddress=webAddress;
		setModified(WEB_ADDRESS, oldWebAddress, this.webAddress);
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

	public MediaFile getLogo()
	{
		return (MediaFile)getReference(LOGO);
	}

	public void setLogo(MediaFile logo)
	{
		setReference(LOGO, logo);
	}

	public boolean isReceivable()
	{
		return MediaConfiguration.isChannelReceivable(this);
	}

	public void setReceivable(boolean receivable)
	{
		boolean oldReceivable=isReceivable();
		if (receivable!=oldReceivable)
		{
			MediaConfiguration.setChannelReceivable(this, receivable);
			firePropertyChange(RECEIVABLE, oldReceivable, receivable);
		}
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
	public String toString()
	{
		if (name!=null) return name;
		return super.toString();
	}

	@Override
	public void delete()
	{
		super.delete();
		MediaConfiguration.removeChannel(this);
	}

	@Override
	public void afterReload()
	{
		altNames=null;
		super.afterReload();
	}

	@Override
	public boolean isUsed()
	{
		return super.isUsed() || ChannelManager.getInstance().isChannelUsed(this);
	}
}
