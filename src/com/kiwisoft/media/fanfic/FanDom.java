/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 21, 2003
 * Time: 6:56:46 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Set;
import java.util.SortedSet;

import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.DBAssociation;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.Link;

public class FanDom extends IDObject implements FanFicGroup, Comparable, Linkable
{
	public static final String SHOW="show";
	public static final String MOVIE="movie";
	public static final String FANFICS="fanfics";
	public static final String LINKS="links";

	private static final DBAssociation<FanDom, FanFic> ASSOCIATIONS_FANFICS=DBAssociation.getAssociation(FANFICS, FanDom.class, FanFic.class);

	private String name;
	private Set<Link> links;

	public FanDom()
	{
	}

	public FanDom(DBDummy dummy)
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

	public Set getFanFics()
	{
		return ASSOCIATIONS_FANFICS.getAssociations(this);
	}

	public int getFanFicCount()
	{
		return ASSOCIATIONS_FANFICS.getAssociationsSize(this);
	}

	public boolean contains(FanFic fanFic)
	{
		return ASSOCIATIONS_FANFICS.isExistsAssociation(this, fanFic);
	}

	public SortedSet getFanFicLetters()
	{
		return FanFicManager.getInstance().getFanFicLetters(this);
	}

	public Set getFanFics(char ch)
	{
		return FanFicManager.getInstance().getFanFics(this, ch);
	}

	public String getHttpParameter()
	{
		return "fandom="+getId();
	}

	public String toString()
	{
		return getName();
	}

	public int compareTo(Object o)
	{
		return getName().compareToIgnoreCase(((FanDom)o).getName());
	}

	public Link createLink()
	{
		Link link=new Link(this);
		if (links!=null) links.add(link);
		fireElementAdded(LINKS, link);
		return link;
	}

	public void dropLink(Link link)
	{
		if (links!=null) links.remove(link);
		link.delete();
		fireElementRemoved(LINKS, link);
	}

	public Set<Link> getLinks()
	{
		if (links==null) links=DBLoader.getInstance().loadSet(Link.class, null, "fandom_id=?", getId());
		return links;
	}

	public int getLinkCount()
	{
		if (links!=null) return links.size();
		else return DBLoader.getInstance().count(Link.class, null, "fandom_id=?", getId());
	}
}
