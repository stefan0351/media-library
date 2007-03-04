/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 21, 2003
 * Time: 6:56:46 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Set;
import java.util.SortedSet;

import com.kiwisoft.utils.db.DBAssociation;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;

public class Pairing extends IDObject implements FanFicGroup, Comparable
{
	public static final String FANFICS="fanfics";

	private static final DBAssociation<Pairing, FanFic> ASSOCIATIONS_FANFICS=DBAssociation.getAssociation(FANFICS, Pairing.class, FanFic.class);

	private String name;

	public Pairing()
	{
	}

	public Pairing(DBDummy dummy)
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
		return "pairing="+getId();
	}

	public String toString()
	{
		return getName();
	}

	public int compareTo(Object o)
	{
		return getName().compareToIgnoreCase(((Pairing)o).getName());
	}
}
