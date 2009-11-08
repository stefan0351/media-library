package com.kiwisoft.media.books;

import java.util.Comparator;

import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class BookSeriesComparator implements Comparator<Book>
{
	@Override
	public int compare(Book book1, Book book2)
	{
		Integer number1=book1.getSeriesNumber();
		Integer number2=book2.getSeriesNumber();
		if (number1==null && number2!=null) return 1;
		if (number2==null && number1!=null) return -1;
		int result=0;
		if (number1!=null) result=number1.compareTo(number2);
		if (result==0) Utils.compareNullSafe(book1.getTitle(), book2.getTitle(), false);
		return result;
	}
}