package com.kiwisoft.web;

import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class RecentIdObject<T extends IDObject> implements RecentItem
{
	private Class<T> clazz;
	private Long id;

	public RecentIdObject(Class<T> clazz, T object)
	{
		this.clazz=clazz;
		this.id=object.getId();
	}

	@Override
	public String getItemClassId()
	{
		return clazz.getName();
	}

	public T getObject()
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
