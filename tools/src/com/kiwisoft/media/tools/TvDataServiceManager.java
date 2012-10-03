package com.kiwisoft.media.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Stefan Stiller
 * @since 06.03.11
 */
public class TvDataServiceManager
{
	private static TvDataServiceManager instance;

	private Map<String, TvDataService> services=new HashMap<String, TvDataService>();

	private TvDataServiceManager()
	{
	}

	public static TvDataServiceManager getInstance()
	{
		if (instance==null) instance=new TvDataServiceManager();
		return instance;
	}

	public void registerService(TvDataService service)
	{
		services.put(service.getId(), service);
	}

	public TvDataService getService(String id)
	{
		return services.get(id);
	}

	public Collection<TvDataService> getServices()
	{
		return services.values();
	}
}
