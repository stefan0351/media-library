/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 21, 2003
 * Time: 6:56:46 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Set;
import java.util.SortedSet;

import com.kiwisoft.media.ContactMedium;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBLoader;

public class Author extends IDObject implements FanFicGroup, Comparable
{
	public static final String MAIL="mail";
	public static final String WEB="web";
	public static final String FANFICS="fanfics";

	private String name;
	private String path;
	private Set<ContactMedium> mail;
	private Set<ContactMedium> web;

	public Author()
	{
	}

	public Author(DBDummy dummy)
	{
		super(dummy);
	}

	public String getFanFicGroupName()
	{
		return getName();
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

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path=path;
	}

	public boolean isUsed()
	{
		return FanFicManager.getInstance().isAuthorUsed(this);
	}

	public Set<ContactMedium> getMail()
	{
		if (mail==null)
			mail=DBLoader.getInstance().loadSet(ContactMedium.class, null, "type=? and author_id=?", ContactMedium.MAIL, getId());
		return mail;
	}

	public ContactMedium createMail()
	{
		ContactMedium medium=new ContactMedium(this, ContactMedium.MAIL);
		if (mail!=null) mail.add(medium);
		fireElementAdded(MAIL, medium);
		return medium;
	}

	public void dropMail(ContactMedium medium)
	{
		medium.delete();
		if (mail!=null) mail.remove(medium);
		fireElementRemoved(MAIL, medium);
	}

	public Set<ContactMedium> getWeb()
	{
		if (web==null)
			web=DBLoader.getInstance().loadSet(ContactMedium.class, null, "type=? and author_id=?", ContactMedium.WEB, getId());
		return web;
	}

	public ContactMedium createWeb()
	{
		ContactMedium medium=new ContactMedium(this, ContactMedium.WEB);
		if (web!=null) web.add(medium);
		fireElementAdded(WEB, medium);
		return medium;
	}

	public void dropWeb(ContactMedium medium)
	{
		medium.delete();
		if (web!=null) web.remove(medium);
		fireElementRemoved(WEB, medium);
	}

	public Set<FanFic> getFanFics()
	{
		return getAssociations(FANFICS);
	}

	public int getFanFicCount()
	{
		return getAssociationsCount(FANFICS);
	}

	public boolean contains(FanFic fanFic)
	{
		return containsAssociation(FANFICS, fanFic);
	}

	public SortedSet<Character> getFanFicLetters()
	{
		return FanFicManager.getInstance().getFanFicLetters(this);
	}

	public Set<FanFic> getFanFics(char ch)
	{
		return FanFicManager.getInstance().getFanFics(this, ch);
	}

	public String getHttpParameter()
	{
		return "author="+getId();
	}

	public void afterReload()
	{
		mail=null;
		web=null;
		super.afterReload();
	}

	public String toString()
	{
		return getName();
	}

	public int compareTo(Object o)
	{
		return getName().compareToIgnoreCase(((Author)o).getName());
	}
}
