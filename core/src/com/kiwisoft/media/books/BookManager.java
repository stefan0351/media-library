package com.kiwisoft.media.books;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kiwisoft.utils.Bean;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;

/**
 * @author Stefan Stiller
 */
public class BookManager extends Bean
{
	public static final String BOOKS="books";

	private static BookManager instance;

	public static BookManager getInstance()
	{
		if (instance==null) instance=new BookManager();
		return instance;
	}

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
		return DBLoader.getInstance().loadSet(Book.class, null, "sort_letter(index_by)=?", String.valueOf(ch));
	}

	public SortedSet<Character> getLetters()
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct sort_letter(index_by) from books");
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
            if (Character.isDigit(ch) || ch=='X' || ch=='x') filteredId.append(ch);
        }
        return filteredId.toString();
    }

	public Collection<String> getPublishers()
	{
		try
		{
			Set<String> publishers=new HashSet<String>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct publisher from books");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next()) publishers.add(resultSet.getString(1));
			}
			finally
			{
				statement.close();
			}
			return publishers;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Collection<String> getBindings()
	{
		try
		{
			Set<String> publishers=new HashSet<String>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct binding from books");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next()) publishers.add(resultSet.getString(1));
			}
			finally
			{
				statement.close();
			}
			return publishers;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Collection<String> getSeriesNames()
	{
		try
		{
			Set<String> publishers=new HashSet<String>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct series_name from books where series_name is not null");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next()) publishers.add(resultSet.getString(1));
			}
			finally
			{
				statement.close();
			}
			return publishers;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Set<Book> getBooksBySeries(String seriesName)
	{
		return DBLoader.getInstance().loadSet(Book.class, null, "series_name=?", seriesName);
	}

	public Collection<String> getStorages()
	{
		try
		{
			Set<String> storages=new HashSet<String>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct storage from books");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next()) storages.add(resultSet.getString(1));
			}
			finally
			{
				statement.close();
			}
			return storages;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Set<Book> getBooksByISBN(Isbn isbn)
	{
		if (isbn!=null)
		{
			Set<Book> books=new HashSet<Book>();
			books.addAll(DBLoader.getInstance().loadSet(Book.class, null, "replace(replace(ISBN10, ' ', ''), '-', '')=?", isbn.getIsbn10().getRawNumber()));
			books.addAll(DBLoader.getInstance().loadSet(Book.class, null, "replace(replace(ISBN13, ' ', ''), '-', '')=?", isbn.getIsbn13().getRawNumber()));
			return books;
		}
		return null;
	}

	public Set<Book> getBooksByTitle(String title)
	{
		return DBLoader.getInstance().loadSet(Book.class, null, "title=?", title);
	}
}
