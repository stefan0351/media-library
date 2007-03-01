/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 28, 2003
 * Time: 8:46:55 PM
 */
package com.kiwisoft.media.show;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import com.kiwisoft.utils.gui.lookup.ListLookup;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;

public class EpisodeLookup extends ListLookup<Episode>
{
	private Show show;

	public EpisodeLookup(Show show)
	{
		this.show=show;
	}

	protected Show getShow()
	{
		return show;
	}

	public Collection<Episode> getValues(String text, Episode currentValue, boolean lookup)
	{
		Show show=getShow();
		if (show!=null)
		{
			if (text==null)
				return show.getEpisodes().elements();
			else
			{
				String title;
				if (text.indexOf('*')>=0)
					title=text.replace('*', '%');
				else
					title=text+"%";
				Set<Episode> episodes=new HashSet<Episode>();
				DBLoader dbLoader=DBLoader.getInstance();
				episodes.addAll(dbLoader.loadSet(Episode.class, null,
						"show_id=? and (name like ? or userkey=? or name_original like ?)",
						show.getId(), title, text, title));
				episodes.addAll(dbLoader.loadSet(Episode.class, "names",
						"show_id=? and names.ref_id=episodes.id and names.name like ?",
						show.getId(), title));
				return episodes;
			}
		}
		return Collections.emptySet();
	}
}
