/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import com.kiwisoft.persistence.DBLoader;

public class LanguageManager
{
	private static LanguageManager instance;

	public static final Language GERMAN=DBLoader.getInstance().load(Language.class, null, "symbol=?", "de");
	public static final Language ENGLISH=DBLoader.getInstance().load(Language.class, null, "symbol=?", "en");

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

	public Map<String, Language> symbolMap=new HashMap<String, Language>();

	public Language getLanguageBySymbol(String symbol)
	{
		if (symbolMap.containsKey(symbol))
		{
			return symbolMap.get(symbol);
		}
		else
		{
			Language language=DBLoader.getInstance().load(Language.class, null, "symbol=?", symbol);
			symbolMap.put(symbol, language);
			return language;
		}
	}

	public Language getLanguageByName(String name)
	{
		return DBLoader.getInstance().load(Language.class, null, "name=?", name);
	}
}

