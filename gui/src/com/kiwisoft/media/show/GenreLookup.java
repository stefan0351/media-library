/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.show;

import java.util.Collection;
import java.util.Collections;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.media.Genre;

public class GenreLookup extends ListLookup<Genre>
{
	@Override
	public Collection<Genre> getValues(String text, Genre currentValue, int lookup)
	{
		if (lookup>0) return DBLoader.getInstance().loadSet(Genre.class);
		if (text==null) return Collections.emptySet();
		if (text.indexOf('*')>=0) text=text.replace('*', '%');
		else text=text+"%";
		return DBLoader.getInstance().loadSet(Genre.class, null, "name like ?", text);
	}
}
