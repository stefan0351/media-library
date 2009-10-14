package com.kiwisoft.media.fanfics;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.Author;

import java.util.TreeSet;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ListFanFicAuthorsAction extends BaseAction
{
	private Character letter;
	private Set<Author> authors;
	private SortedSet<Character> letters;

	@Override
	public String getPageTitle()
	{
		return "Fan Fiction - Authors";
	}

	@Override
	public String execute() throws Exception
	{
		letters=FanFicManager.getInstance().getAuthorLetters();
		if (letter==null)
		{
			if (letters.isEmpty()) letter='A';
			else letter=letters.first();
		}
		authors=new TreeSet<Author>(FanFicManager.getInstance().getAuthors(letter.charValue()));
		return super.execute();
	}

	public Character getLetter()
	{
		return letter;
	}

	public void setLetter(Character letter)
	{
		this.letter=letter;
	}

	public Set<Author> getAuthors()
	{
		return authors;
	}

	public SortedSet<Character> getLetters()
	{
		return letters;
	}
}