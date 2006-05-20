/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;

public class ChannelManager
{
	public static final String CHANNELS="channels";

	private static ChannelManager instance;

	public synchronized static ChannelManager getInstance()
	{
		if (instance==null) instance=new ChannelManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private ChannelManager()
	{
	}

	public Set<Channel> getChannels()
	{
		return DBLoader.getInstance().loadSet(Channel.class);
	}

	public Channel createChannel()
	{
		Channel channel=new Channel();
		fireElementAdded(CHANNELS, channel);
		return channel;
	}

	public void dropChannel(Channel channel)
	{
		channel.delete();
		fireElementRemoved(CHANNELS, channel);
	}

	public int getChannelCount()
	{
		return DBLoader.getInstance().count(Channel.class, null, null);
	}

	public Channel getChannelByName(String name)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		Channel channel=dbLoader.load(Channel.class, null, "name=?", name);
		if (channel==null)
		{
			channel=dbLoader.load(Channel.class, "names", "names.ref_id=channels.id and names.name=?", name);
		}
		return channel;
	}

	public boolean isChannelUsed(Channel channel)
	{
		return DBLoader.getInstance().count(Airdate.class, null, "channel_id=?", channel.getId())>0;
	}

	public void addCollectionChangeListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	protected void fireElementAdded(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementAdded(propertyName, element);
	}

	protected void fireElementRemoved(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementRemoved(propertyName, element);
	}

}

