package com.kiwisoft.media.books;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 * @since 30.10.2009
 */
public class BookFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object value)
	{
		if (value instanceof Book)
		{
			Book book=(Book) value;
			return book.getTitle();			
		}
		return super.format(value);
	}
}
