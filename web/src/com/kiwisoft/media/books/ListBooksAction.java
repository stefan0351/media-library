package com.kiwisoft.media.books;

import com.kiwisoft.media.BaseAction;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Stefan Stiller
 * @since 02.10.2009
 */
public class ListBooksAction extends BaseAction
{
	private Character letter;
	private SortedSet<Character> letters;
	private Set<Book> books;

	@Override
	public String getPageTitle()
	{
		return "Books";
	}

	@Override
	public String execute() throws Exception
	{
		letters=BookManager.getInstance().getLetters();
		if (letter==null)
		{
			if (letters.size()>1) letter=letters.first();
			else letter='A';
		}
		books=new TreeSet<Book>(new BookComparator());
		books.addAll(BookManager.getInstance().getBooksByLetter(letter));
		return super.execute();
	}

	public SortedSet<Character> getLetters()
	{
		return letters;
	}

	public Set<Book> getBooks()
	{
		return books;
	}

	public Character getLetter()
	{
		return letter;
	}

	public void setLetter(Character letter)
	{
		this.letter=letter;
	}
}
