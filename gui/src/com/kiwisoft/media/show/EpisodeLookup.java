/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 28, 2003
 * Time: 8:46:55 PM
 */
package com.kiwisoft.media.show;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.lookup.TableLookup;
import com.kiwisoft.swing.table.TableConfiguration;
import com.kiwisoft.swing.table.DefaultTableConfiguration;

public class EpisodeLookup extends TableLookup<Episode>
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
		if (show==null) return Collections.emptySet();
		if (lookup) return show.getEpisodes().elements();
		if (StringUtils.isEmpty(text)) return Collections.emptySet();
		String title;
		if (text.indexOf('*')>=0) title=text.replace('*', '%');
		else title=text+"%";
		Set<Episode> episodes=new HashSet<Episode>();
		DBLoader dbLoader=DBLoader.getInstance();
		episodes.addAll(dbLoader.loadSet(Episode.class, null, "show_id=? and (title like ? or userkey=? or german_title like ?)",
										 show.getId(), title, text, title));
		episodes.addAll(dbLoader.loadSet(Episode.class, "names", "show_id=? and names.ref_id=episodes.id and names.name like ?",
										 show.getId(), title));
		return episodes;
	}

	protected TableConfiguration getTableConfiguration()
	{
		return new DefaultTableConfiguration(EpisodeLookup.class);
	}

	public String[] getColumnNames()
	{
		return new String[]{"key", "title", "germanTitle"};
	}

	public Object getColumnValue(Episode episode, int column, String property)
	{
		if ("key".equals(property)) return episode.getUserKey();
		if ("title".equals(property)) return episode.getTitle();
		if ("germanTitle".equals(property)) return episode.getGermanTitle();
		return null;
	}

	@Override
	public Comparable getColumnSortValue(Episode episode, int column, String property)
	{
		if ("key".equals(property)) return episode.getChainPosition();
		return super.getColumnSortValue(episode, column, property);
	}
}
