package com.kiwisoft.web;

import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Utils;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class RecentIdObject<T extends IDObject> implements RecentItem<T>
{
	private Class<T> clazz;
	private Long id;

	public RecentIdObject()
	{
	}

	public RecentIdObject(Class<T> clazz, T object)
	{
		this.clazz=clazz;
		this.id=object.getId();
	}

	@Override
	public Map<String, String> getProperties()
	{
		Map<String, String> properties=new HashMap<String, String>();
		properties.put("class", clazz.getName());
		properties.put("id", id.toString());
		return properties;
	}

	@Override
	public void setProperties(Map<String, String> properties) throws Exception
	{
		clazz=Utils.cast(Class.forName(properties.get("class")));
		id=Long.valueOf(properties.get("id"));
	}

	@Override
	public T getItem()
	{
		return DBLoader.getInstance().load(clazz, id);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (!(o instanceof RecentIdObject)) return false;

		RecentIdObject that=(RecentIdObject) o;

		if (clazz!=null ? !clazz.equals(that.clazz) : that.clazz!=null) return false;
		if (id!=null ? !id.equals(that.id) : that.id!=null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result=clazz!=null ? clazz.hashCode() : 0;
		result=31*result+(id!=null ? id.hashCode() : 0);
		return result;
	}
}
