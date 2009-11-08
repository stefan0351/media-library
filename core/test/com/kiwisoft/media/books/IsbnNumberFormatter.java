package com.kiwisoft.media.books;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.StringUtils;

import java.util.Locale;
import java.util.List;
import java.io.File;

/**
 * @author Stefan Stiller
 * @since 31.10.2009
 */
public class IsbnNumberFormatter
{
	private IsbnNumberFormatter()
	{
	}

	public static void main(String[] args)
	{
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);
		configuration.loadUserValues("media"+File.separator+"profile.xml");

		List<Object> bookIds=DBLoader.getInstance().loadKeys(Book.class, null, null);
		for (final Object bookId : bookIds)
		{
			 DBSession.execute(new Transactional()
			 {
				 @Override
				 public void run() throws Exception
				 {
					 Book book=BookManager.getInstance().getBook((Long) bookId);
					 if (!StringUtils.isEmpty(book.getIsbn10()))
					 {
						 book.setIsbn10(Isbn.valueOf(book.getIsbn10()).toString());
					 }
					 if (!StringUtils.isEmpty(book.getIsbn13()))
					 {
						 book.setIsbn13(Isbn.valueOf(book.getIsbn13()).toString());
					 }
				 }

				 @Override
				 public void handleError(Throwable throwable, boolean rollback)
				 {
					 throwable.printStackTrace();
				 }
			 });
		}

	}
}
