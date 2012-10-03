package com.kiwisoft.media.tools;

import com.kiwisoft.media.tvdata.Channel;
import com.kiwisoft.media.tvdata.ChannelGroup;

import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 06.03.11
 */
public interface TvDataService
{
	String getId();

	void refreshChannelGroups() throws TvDataException;

	Set<ChannelGroup> getChannelGroups();

	void refreshChannels(ChannelGroup channelGroup) throws TvDataException;

	Set<Channel> getChannels(ChannelGroup channelGroup);
}
