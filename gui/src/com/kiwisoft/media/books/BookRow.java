package com.kiwisoft.media.books;

import com.kiwisoft.swing.table.BeanTableRow;
import com.kiwisoft.utils.CompoundComparable;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class BookRow extends BeanTableRow<Book>
{
	public static final String ISBN="isbn";

	public BookRow(Book book)
	{
		super(book);
	}

	@Override
	public Comparable getSortValue(int column, String property)
	{
		if (Book.TITLE.equals(property)) return getUserObject().getIndexBy();
		if (Book.SERIES_TITLE.equals(property))
			return new CompoundComparable<String, Integer>(getUserObject().getSeriesName(), getUserObject().getSeriesNumber());
		return super.getSortValue(column, property);
	}

	@Override
	public Object getDisplayValue(int column, String property)
	{
		if (Book.AUTHORS.equals(property)) return StringUtils.formatAsEnumeration(getUserObject().getAuthors(), "; ");
		if (Book.PAGE_COUNT.equals(property))
		{
			Integer pageCount=getUserObject().getPageCount();
			if (pageCount!=null && pageCount>0) return pageCount;
			return null;
		}
		if (ISBN.equals(property))
		{
			String isbn=getUserObject().getIsbn13();
			if (StringUtils.isEmpty(isbn)) isbn=getUserObject().getIsbn10();
			return isbn;
		}
		return super.getDisplayValue(column, property);
	}
}
