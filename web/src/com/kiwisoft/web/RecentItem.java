package com.kiwisoft.web;

import java.util.Map;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public interface RecentItem<T>
{
	void setProperties(Map<String, String> properties) throws Exception;

	Map<String, String> getProperties();

	T getItem();
}
