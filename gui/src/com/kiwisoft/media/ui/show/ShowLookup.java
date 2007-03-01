/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.ui.show;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class ShowLookup extends ListLookup<Show>
{
	public Collection<Show> getValues(String text, Show currentValue, boolean lookup)
	{
		if (text==null) return ShowManager.getInstance().getShows();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			Set<Show> shows=new HashSet<Show>();
			DBLoader dbLoader=DBLoader.getInstance();
			shows.addAll(dbLoader.loadSet(Show.class, null, "name like ? or name_original like ?", text, text));
			shows.addAll(dbLoader.loadSet(Show.class, "names", "names.ref_id=shows.id and names.name like ?", text));
			return shows;
		}
	}

}
