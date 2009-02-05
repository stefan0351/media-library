package com.kiwisoft.media.books;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeSource;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;

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

	public Set<Book> getBooksByLetter(char ch)
	{
		return DBLoader.getInstance().loadSet(Book.class, null, "sort_letter(title)=?", String.valueOf(ch));
	}

	public SortedSet<Character> getLetters()
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct sort_letter(title) from books");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next())
				{
					String string=resultSet.getString(1);
					if (string!=null && string.length()>0)
						set.add(new Character(string.charAt(0)));
				}
			}
			finally
			{
				statement.close();
			}
			return set;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Disposable addCollectionListener(CollectionChangeListener listener)
	{
		return collectionChangeSupport.addListener(listener);
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

	public int getBookCount()
	{
		return DBLoader.getInstance().count(Book.class);
	}

	public Book getBook(Long id)
	{
		return DBLoader.getInstance().load(Book.class, id);
	}

    public static String filterIsbn(String id)
    {
        StringBuilder filteredId=new StringBuilder();
        for (int i=0;i<id.length();i++)
        {
            char ch=id.charAt(i);
            if (Character.isDigit(ch)) filteredId.append(ch);
        }
        return filteredId.toString();
    }
}
