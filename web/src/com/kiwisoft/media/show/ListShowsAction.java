package com.kiwisoft.media.show;

import com.kiwisoft.media.BaseAction;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 02.10.2009
 */
public class ListShowsAction extends BaseAction
{
	private String letter;
	private SortedSet<Character> letters;
	private Set<Show> shows;

	@Override
	public String getPageTitle()
	{
		return "Shows";
	}

	@Override
	public String execute() throws Exception
	{
		letters=ShowManager.getInstance().getLetters();
		shows=new TreeSet<Show>(new ShowComparator());
		if ("all".equalsIgnoreCase(letter))
		{
			shows.addAll(ShowManager.getInstance().getShows());
			letter="All";
		}
		else
		{
			char selectedLetter=letter!=null && letter.length()==1 ? letter.charAt(0) : letters.first().charValue();
			shows.addAll(ShowManager.getInstance().getShowsByLetter(selectedLetter));
			letter=String.valueOf(selectedLetter);
		}
		return super.execute();
	}

	public String getLetter()
	{
		return letter;
	}

	public void setLetter(String letter)
	{
		this.letter=letter;
	}

	public Set<Character> getLetters()
	{
		return letters;
	}

	public Set<Show> getShows()
	{
		return shows;
	}
}
