/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.persistence.DBLoader;

public class PairingLookup extends ListLookup<Pairing>
{
	@Override
	public Collection<Pairing> getValues(String text, Pairing currentValue, int lookup)
	{
		if (lookup>0) return FanFicManager.getInstance().getPairings();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text="%"+text+"%";
			return DBLoader.getInstance().loadSet(Pairing.class, null, "name like ?", text);
		}
	}

}
