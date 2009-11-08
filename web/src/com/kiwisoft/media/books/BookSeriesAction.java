package com.kiwisoft.media.books;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.utils.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Stefan Stiller
 * @since 01.11.2009
 */
public class BookSeriesAction extends BaseAction
{
	private String seriesName;
	private List<Book> books;

	@Override
	public String getPageTitle()
	{
		return "Books";
	}

	public void setSeriesName(String seriesName)
	{
		this.seriesName=seriesName;
	}

	@Override
	public String execute() throws Exception
	{
		if (!StringUtils.isEmpty(seriesName))
		{
			books=new ArrayList<Book>();
			books.addAll(BookManager.getInstance().getBooksBySeries(seriesName));
			Collections.sort(books, new BookSeriesComparator());
		}
		return super.execute();
	}

	public String getSeriesName()
	{
		return seriesName;
	}

	public List<Book> getBooks()
	{
		return books;
	}
}
