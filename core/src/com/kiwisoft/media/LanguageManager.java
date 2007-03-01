/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.utils.db.DBLoader;

public class LanguageManager
{
	private static LanguageManager instance;

	public synchronized static LanguageManager getInstance()
	{
		if (instance==null) instance=new LanguageManager();
		return instance;
	}

	private LanguageManager()
	{
	}

	public Set<Language> getLanguages()
	{
		return DBLoader.getInstance().loadSet(Language.class);
	}

	public Language getLanguageBySymbol(String symbol)
	{
		return DBLoader.getInstance().load(Language.class, null, "symbol=?", symbol);
	}
}

