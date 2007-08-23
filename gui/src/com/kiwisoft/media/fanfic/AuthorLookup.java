/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;

import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.Author;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class AuthorLookup extends ListLookup<Author>
{
	public Collection<Author> getValues(String text, Author currentValue, boolean lookup)
	{
		if (text==null) return FanFicManager.getInstance().getAuthors();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(Author.class, null, "name like ?", text);
		}
	}

}
