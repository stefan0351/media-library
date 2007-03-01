/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;

import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.Pairing;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class PairingLookup extends ListLookup<Pairing>
{
	public Collection<Pairing> getValues(String text, Pairing currentValue, boolean lookup)
	{
		if (text==null) return FanFicManager.getInstance().getPairings();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text="%"+text+"%";
			return DBLoader.getInstance().loadSet(Pairing.class, null, "name like ?", text);
		}
	}

}
