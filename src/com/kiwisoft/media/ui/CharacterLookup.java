/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.ui;

import java.util.Collection;
import java.util.Collections;

import com.kiwisoft.media.ShowCharacter;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class CharacterLookup extends ListLookup<ShowCharacter>
{
	public Collection<ShowCharacter> getValues(String text, ShowCharacter currentValue)
	{
		if (text==null) return Collections.emptySet();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(ShowCharacter.class, null, "name like ? or nickname like ?", text, text);
		}
	}

}
