package com.kiwisoft.media.links;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.lookup.ListLookup;

/**
 * @author Stefan Stiller
 */
public class LinkableLookup extends ListLookup<Linkable>
{
	public LinkableLookup()
	{
		setFormatVariant("linkable");
	}

	public Collection<Linkable> getValues(String text, Linkable currentValue, boolean lookup)
	{
		if (lookup)
		{
			Set<Linkable> linkables=new HashSet<Linkable>();
			DBLoader dbLoader=DBLoader.getInstance();
			linkables.addAll(dbLoader.loadSet(Show.class, null, "linkgroup_id is null"));
			linkables.addAll(dbLoader.loadSet(FanDom.class, null, "linkgroup_id is null"));
			linkables.addAll(dbLoader.loadSet(LinkGroup.class));
			return linkables;
		}
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			Set<Linkable> linkables=new HashSet<Linkable>();
			DBLoader dbLoader=DBLoader.getInstance();
			linkables.addAll(dbLoader.loadSet(Show.class, null, "linkgroup_id is null and (title like ? or german_title like ?)", text, text));
			linkables.addAll(dbLoader.loadSet(Show.class, "names", "linkgroup_id is null and (names.ref_id=shows.id and names.name like ?)", text));
			linkables.addAll(dbLoader.loadSet(FanDom.class, null, "linkgroup_id is null and concat(name,' - FanFic') like ?", text));
			linkables.addAll(dbLoader.loadSet(LinkGroup.class, null, "name like ?", text));
			return linkables;
		}
	}
}
