package com.kiwisoft.media.show;

import java.util.Iterator;
import java.util.ResourceBundle;

import com.kiwisoft.media.books.Book;
import com.kiwisoft.web.SortableWebTable;
import com.kiwisoft.web.TableSortDescription;
import com.kiwisoft.web.TableConstants;

/**
 * @author Stefan Stiller
 */
public class ShowBooksTable extends SortableWebTable<Book>
{
	public static final String TITLE="title";
	public static final String AUTHOR="author";
	public static final String LANGUAGE="language";

	public ShowBooksTable(Show show)
	{
		super(TITLE, AUTHOR, LANGUAGE);
		initializeData(show);
	}

	private void initializeData(Show show)
	{
		Iterator<Book> it=show.getBooks().iterator();
		while (it.hasNext()) addRow(new BookRow(it.next()));
		setSortColumn(new TableSortDescription(0, TableConstants.ASCEND));
		sort();
	}

	@Override
	public ResourceBundle getBundle()
	{
		return ResourceBundle.getBundle(ShowBooksTable.class.getName());
	}

	private static class BookRow extends Row<Book>
	{
		public BookRow(Book book)
		{
			super(book);
		}

		@Override
		public Comparable getSortValue(int column, String property)
		{
			if (TITLE.equals(property)) return getUserObject().getIndexBy();
			return super.getSortValue(column, property);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			Book book=getUserObject();
			if (TITLE.equals(property)) return book;
			else if (AUTHOR.equals(property)) return book.getAuthors();
			else if (LANGUAGE.equals(property)) return book.getLanguage();
			return null;
		}
	}

}