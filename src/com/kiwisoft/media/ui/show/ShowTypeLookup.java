/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.ui.show;

import java.util.Collection;

import com.kiwisoft.media.show.ShowType;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class ShowTypeLookup extends ListLookup<ShowType>
{
	public Collection<ShowType> getValues(String text, ShowType currentValue)
	{
		if (text==null) return ShowType.getAll();
		else
		{
			if (text.indexOf('*')<0) text=text+"*";
			return ShowType.getAll(text);
		}
	}

}
