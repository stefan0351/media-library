/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;

import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.lookup.ListLookup;

public class AuthorLookup extends ListLookup<Author>
{
	@Override
	public Collection<Author> getValues(String text, Author currentValue, int lookup)
	{
		if (lookup>0) return FanFicManager.getInstance().getAuthors();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(Author.class, null, "name like ?", text);
		}
	}

}
