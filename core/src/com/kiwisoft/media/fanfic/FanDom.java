/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 21, 2003
 * Time: 6:56:46 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Set;
import java.util.SortedSet;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;

public class FanDom extends IDObject implements FanFicGroup, Comparable, Linkable
{
	public static final String SHOW="show";
	public static final String MOVIE="movie";
	public static final String FANFICS="fanfics";
	public static final String NAME="name";

	private String name;

	public FanDom()
	{
	}

	public FanDom(DBDummy dummy)
	{
		super(dummy);
	}

	@Override
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
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, this.name);
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public Movie getMovie()
	{
		return (Movie)getReference(MOVIE);
	}

	public void setMovie(Movie movie)
	{
		setReference(MOVIE, movie);
	}

	@Override
	public Set<FanFic> getFanFics()
	{
		return getAssociations(FANFICS);
	}

	@Override
	public int getFanFicCount()
	{
		return getAssociationsCount(FANFICS);
	}

	@Override
	public boolean contains(FanFic fanFic)
	{
		return containsAssociation(FANFICS, fanFic);
	}

	@Override
	public SortedSet<Character> getFanFicLetters()
	{
		return FanFicManager.getInstance().getFanFicLetters(this);
	}

	@Override
	public Set<FanFic> getFanFics(char ch)
	{
		return FanFicManager.getInstance().getFanFics(this, ch);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public int compareTo(Object o)
	{
		return getName().compareToIgnoreCase(((FanDom)o).getName());
	}

	public LinkGroup getLinkGroup()
	{
		return (LinkGroup)getReference(LINK_GROUP);
	}

	@Override
	public LinkGroup getLinkGroup(boolean create)
	{
		LinkGroup group=getLinkGroup();
		if (group==null && create) setLinkGroup(group=LinkManager.getInstance().createRootGroup(getName()+" - FanFic"));
		return group;
	}

	public void setLinkGroup(LinkGroup group)
	{
		setReference(LINK_GROUP, group);
	}
}
