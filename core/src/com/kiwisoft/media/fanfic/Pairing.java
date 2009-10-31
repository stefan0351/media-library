/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 21, 2003
 * Time: 6:56:46 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Set;
import java.util.SortedSet;

import com.kiwisoft.persistence.DBAssociation;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;

public class Pairing extends IDObject implements FanFicGroup, Comparable
{
	public static final String FANFICS="fanfics";

	private String name;

	public Pairing()
	{
	}

	public Pairing(DBDummy dummy)
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
		setModified("name", oldName, this.name);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public Set<FanFic> getFanFics()
	{
		return (Set<FanFic>)DBAssociation.getAssociation(Pairing.class, FANFICS).getAssociations(this);
	}

	@Override
	public int getFanFicCount()
	{
		return DBAssociation.getAssociation(Pairing.class, FANFICS).getAssociationsSize(this);
	}

	@Override
	public boolean contains(FanFic fanFic)
	{
		return DBAssociation.getAssociation(Pairing.class, FANFICS).isExistsAssociation(this, fanFic);
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
		return getName().compareToIgnoreCase(((Pairing)o).getName());
	}
}
