package com.kiwisoft.media.books;

import java.util.Comparator;

import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class BookComparator implements Comparator<Book>
{
	@Override
	public int compare(Book book1, Book book2)
	{
		int result=Utils.compareNullSafe(book1.getTitle(), book2.getTitle(), false);
		if (result==0) result=book1.getId().compareTo(book2.getId());
		return result;
	}
}
