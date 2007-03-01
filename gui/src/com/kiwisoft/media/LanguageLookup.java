/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media;

import java.util.Collection;

import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.Language;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class LanguageLookup extends ListLookup<Language>
{
	public Collection<Language> getValues(String text, Language currentValue, boolean lookup)
	{
		if (text==null) return LanguageManager.getInstance().getLanguages();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(Language.class, null, "name like ? or symbol like ?", text, text);
		}
	}

}
