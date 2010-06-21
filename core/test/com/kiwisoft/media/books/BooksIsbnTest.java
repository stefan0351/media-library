package com.kiwisoft.media.books;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.utils.StringUtils;
import junit.framework.TestCase;

import java.io.File;

/**
 * @author Stefan Stiller
 * @since 31.10.2009
 */
public class BooksIsbnTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);
		configuration.loadUserValues("media"+File.separator+"dev-profile.xml");
	}

	public void testIsbn()
	{
		for (Book book : BookManager.getInstance().getBooks())
		{
			if (!StringUtils.isEmpty(book.getIsbn10()))
			{
				try
				{
					Isbn.valueOf(book.getIsbn10());
				}
				catch (IsbnFormatException e)
				{
					System.err.println("Invalid ISBN: "+book.getIsbn10()+" ("+book.getTitle()+")");
					e.printStackTrace();
				}
			}
			if (!StringUtils.isEmpty(book.getIsbn13()))
			{
				try
				{
					Isbn.valueOf(book.getIsbn13());
				}
				catch (IsbnFormatException e)
				{
					System.err.println("Invalid ISBN: "+book.getIsbn13()+" ("+book.getTitle()+")");
					e.printStackTrace();
				}
			}
		}
	}
}