package com.kiwisoft.media.books;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFileUtils;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class BookDetailsAction extends BaseAction
{
	private Long bookId;
	private Book book;
	private MediaFile cover;
	private ImageFile thumbnail;

	@Override
	public String getPageTitle()
	{
		return book.getTitle();
	}

	@Override
	public String execute() throws Exception
	{
		if (bookId!=null) book=BookManager.getInstance().getBook(bookId);
		if (book!=null)
		{
			cover=book.getCover();
			if (cover!=null)
			{
				thumbnail=cover.getThumbnailSidebar();
				if (thumbnail==null && cover.getWidth()<=MediaFileUtils.THUMBNAIL_SIDEBAR_WIDTH) thumbnail=cover;
			}
		}
		return super.execute();
	}

	public Long getBookId()
	{
		return bookId;
	}

	public void setBookId(Long bookId)
	{
		this.bookId=bookId;
	}

	public Book getBook()
	{
		return book;
	}

	public MediaFile getCover()
	{
		return cover;
	}

	public ImageFile getThumbnail()
	{
		return thumbnail;
	}
}
