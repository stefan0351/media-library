/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media;

import java.util.Collection;
import java.util.Collections;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.persistence.DBLoader;

public class LanguageLookup extends ListLookup<Language>
{
	public Collection<Language> getValues(String text, Language currentValue, boolean lookup)
	{
		if (lookup) return DBLoader.getInstance().loadSet(Language.class);
		if (text==null) return Collections.emptySet();
		String name;
		if (text.indexOf('*')>=0) name=text.replace('*', '%');
		else name=text+"%";
		return DBLoader.getInstance().loadSet(Language.class, null, "name like ? or symbol=?", name, text);
	}

}
