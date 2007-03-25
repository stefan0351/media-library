package com.kiwisoft.media.person;

import java.util.Set;
import java.util.TreeSet;

import com.kiwisoft.media.show.Production;
import com.kiwisoft.utils.SetMap;
import com.kiwisoft.utils.format.FormatStringComparator;

/**
 * @author Stefan Stiller
 */
public class Credits<T>
{
	private Set<Production> productions=new TreeSet<Production>(new FormatStringComparator());
	private SetMap<Production, Production> subProductions=new SetMap<Production, Production>()
	{
		@Override
		protected Set<Production> createSet()
		{
			return new TreeSet<Production>();
		}
	};
	private SetMap<Production, T> creditMap=new SetMap<Production, T>();

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
