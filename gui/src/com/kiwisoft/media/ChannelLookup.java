/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.lookup.ListLookup;

public class ChannelLookup extends ListLookup<Channel>
{
	public Collection<Channel> getValues(String text, Channel currentValue, boolean lookup)
	{
		if (text==null) return ChannelManager.getInstance().getChannels();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			Set<Channel> channels=new HashSet<Channel>();
			DBLoader dbLoader=DBLoader.getInstance();
			channels.addAll(dbLoader.loadSet(Channel.class, null, "name like ?", text));
			channels.addAll(dbLoader.loadSet(Channel.class, "names", "names.ref_id=channels.id and names.name like ?", text));
			return channels;
		}
	}

}
