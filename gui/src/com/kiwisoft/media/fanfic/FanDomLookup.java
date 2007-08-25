/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;

import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.lookup.ListLookup;

public class FanDomLookup extends ListLookup<FanDom>
{
	public Collection<FanDom> getValues(String text, FanDom currentValue, boolean lookup)
	{
		if (text==null) return FanFicManager.getInstance().getDomains();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(FanDom.class, null, "name like ?", text);
		}
	}

}
