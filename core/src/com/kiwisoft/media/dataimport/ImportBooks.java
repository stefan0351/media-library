package com.kiwisoft.media.dataimport;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.format.FormatManager;
import com.kiwisoft.format.FormatUtils;
import com.kiwisoft.media.books.*;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.utils.CSVReader;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.DBLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 09.05.2010
 */
public class ImportBooks
{
	private ImportBooks()
	{
	}

	public static void main(String[] args) throws IOException
	{
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);
		configuration.loadUserValues("media"+File.separator+"profile.xml");
		FormatManager.getInstance().setFormat(Book.class, new BookFormat());

		System.out.println("Database URL: "+configuration.getString("db.url"));

		CSVReader reader=new CSVReader(new File("docs", "books.csv"), "ISO-8859-1");
		try
		{
			List<String> data;
			int skipped=0;
			while ((data=reader.read())!=null)
			{
				if (skipped<1)
				{
					skipped++;
					continue;
				}
				final String title=data.get(0).trim();
				final String originalTitle=data.get(1).trim();
				final String authors=data.get(2).trim();
				String languageString=data.get(3).trim();
				String isbnString=data.get(4).trim();
				final String gdrLicenceString=data.get(5).trim();
				final Language language;
				if (StringUtils.isEmpty(languageString)) language=null;
				else if ("deutsch".equalsIgnoreCase(languageString)) language=LanguageManager.GERMAN;
				else if ("englisch".equalsIgnoreCase(languageString)) language=LanguageManager.ENGLISH;
				else
				{
					System.err.println("Invalid language: "+languageString);
					continue;
				}

				final Isbn isbn;
				if (!StringUtils.isEmpty(isbnString))
				{
					try
					{
						isbn=Isbn.valueOf(isbnString);
						Set<Book> books=BookManager.getInstance().getBooksByISBN(isbn);
						if (!books.isEmpty())
						{
							System.out.println("> Book \""+title+"\" found by ISBN: "+FormatUtils.format(books));
							continue;
						}
					}
					catch (IsbnFormatException e)
					{
						System.err.println("> Invalid Book: "+data);
						System.err.println("> Invalid ISBN: "+isbnString);
						e.printStackTrace();
						continue;
					}
				}
				else isbn=null;
				if (!StringUtils.isEmpty(gdrLicenceString))
				{
					Set<Book> books=DBLoader.getInstance().loadSet(Book.class, null, "gdr_licence=?", gdrLicenceString);
					if (!books.isEmpty())
					{
						System.out.println("> Book \""+title+"\" found by GDR licence number: "+FormatUtils.format(books));
						continue;
					}
				}
				Set<Book> books=BookManager.getInstance().getBooksByTitle(title);
				if (!books.isEmpty())
				{
					System.out.println("> Book \""+title+"\" found by title.");
					continue;
				}
				final String indexBy=Book.createIndexBy(title, null, null, language);

				boolean success=DBSession.execute(new Transactional()
				{
					@Override
					public void run() throws Exception
					{
						Book book=BookManager.getInstance().createBook();
						book.setTitle(title);
						book.setIndexBy(indexBy);
						book.setOriginalTitle(originalTitle);
						book.setLanguage(language);
						if (isbn!=null)
						{
							book.setIsbn10(isbn.getIsbn10().toString());
							book.setIsbn13(isbn.getIsbn13().toString());
						}
						book.setGdrLicence(gdrLicenceString);
						if (!StringUtils.isEmpty(authors) && !"diverse".equals(authors))
						{
							for (String authorName : authors.split(","))
							{
								authorName=authorName.trim();
								Person author=PersonManager.getInstance().getPersonByName(authorName);
								if (author==null)
								{
									author=PersonManager.getInstance().createPerson();
									author.setName(authorName);
								}
								book.addAuthor(author);
							}

						}
					}

					@Override
					public void handleError(Throwable throwable, boolean rollback)
					{
						throwable.printStackTrace();
					}
				});
				if (success) System.out.println("Book \""+title+"\" created successfully.");
			}
		}
		finally
		{
			reader.close();
		}
	}

}
