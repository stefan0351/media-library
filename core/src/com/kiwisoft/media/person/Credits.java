package com.kiwisoft.media.person;

import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

import com.kiwisoft.media.show.Production;
import com.kiwisoft.collection.SetMap;
import com.kiwisoft.collection.SortedSetMap;
import com.kiwisoft.format.FormatStringComparator;

/**
 * @author Stefan Stiller
 */
public class Credits<T>
{
	private Set<Production> productions;
	private SetMap<Production, Production> subProductions;
	private SetMap<Production, T> creditMap;

	public Credits()
	{
		this(null);
	}

	public Credits(Comparator productionComparator)
	{
		if (productionComparator==null) productionComparator=new FormatStringComparator();
		productions=new TreeSet<Production>(productionComparator);
		subProductions=new SortedSetMap<Production, Production>(new FormatStringComparator(), null);
		creditMap=new SetMap<Production, T>();
	}

	public void addProduction(Production production)
	{
		productions.add(production);
	}

	public void addProduction(Production production, Production subProduction)
	{
		productions.add(production);
		subProductions.add(production, subProduction);
	}

	public void addCredit(Production production, T credit)
	{
		creditMap.add(production, credit);
	}

	public Set<Production> getProductions()
	{
		return productions;
	}

	public Set<T> getCredits(Production production)
	{
		return creditMap.get(production);
	}

	public Set<Production> getSubProductions(Production production)
	{
		return subProductions.get(production);
	}

	public boolean isEmpty()
	{
		return productions.isEmpty();
	}
}
