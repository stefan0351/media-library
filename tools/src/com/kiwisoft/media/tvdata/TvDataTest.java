package com.kiwisoft.media.tvdata;

import com.kiwisoft.media.tools.*;

/**
 * @author Stefan Stiller
 * @since 06.03.11
 */
public class TvDataTest
{
	private TvDataTest()
	{
	}

	public static void main(String[] args) throws TvDataException
	{
		TvDataServiceManager.getInstance().registerService(new TvBrowserDataService());

		for (TvDataService dataService : TvDataServiceManager.getInstance().getServices())
		{
			//dataService.refreshChannelGroups();
			for (ChannelGroup channelGroup : dataService.getChannelGroups())
			{
				System.out.println("+"+channelGroup.getName());
				//try
				//{
				//	dataService.refreshChannels(channelGroup);
				//}
				//catch (TvDataException e)
				//{
				//	e.printStackTrace();
				//}
				for (Channel channel : dataService.getChannels(channelGroup))
				{
					System.out.println("+---"+channel.getName());
				}
			}
		}
	}

}
