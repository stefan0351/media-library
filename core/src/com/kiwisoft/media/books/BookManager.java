package com.kiwisoft.media.books;

import java.util.Set;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSource;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;

/**
 * @author Stefan Stiller
 */
public class BookManager implements CollectionChangeSource
{
	public static final String BOOKS="books";

	private static BookManager instance;

	public static BookManager getInstance()
	{
		if (instance==null) instance=new BookManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	public Set<Book> getBooks()
	{
		return DBLoader.getInstance().loadSet(Book.class);
	}

	public Book createBook()
	{
		Book book=new Book();
		fireElementAdded(BOOKS, book);
		return book;
	}

	public void dropBook(Book book)
	{
		book.delete();
		fireElementRemoved(BOOKS, book);
	}

	public void addCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	protected void fireElementAdded(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementAdded(propertyName, element);
	}

	protected void fireElementRemoved(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementRemoved(propertyName, element);
	}
}
