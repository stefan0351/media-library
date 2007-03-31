/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 3, 2002
 * Time: 10:19:33 AM
 */
package com.kiwisoft.xp;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;

public class DefaultXPBean implements XPBean
{
	private Map<String, Object> map;
	private List list;
	private String name;
	private String content;
	private String basePath;

	public DefaultXPBean(XMLContext context, String name)
	{
		this.name=name;
		this.map=new HashMap();
		this.list=new LinkedList();
		basePath=(String)context.getAttribute("path");
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("href".equals(name) || "preview".equals(name))
		{
			String path=new File(basePath).getParent();
			putValue(name, WebUtils.getPath(path, value));
		}
		else putValue(name, value);
	}

	public void setXMLReference(XMLContext context, String name, Object value)
	{
		putValue(name, value);
	}

	public void setXMLContent(XMLContext context, String value)
	{
		content=value;
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof XPBean)
		{
			XPBean bean=(XPBean) element;
			putValue(bean.getName(), element);
		}
		list.add(element);
	}

	public String toString()
	{
		return content;
	}

	public String getName()
	{
		return name;
	}

	private void putValue(String name, Object value)
	{
		Object oldValue=map.get(name);
		if (oldValue==null)
			map.put(name, value);
		else if (oldValue instanceof Collection)
			((Collection) oldValue).add(value);
		else
		{
			List list=new LinkedList();
			list.add(oldValue);
			list.add(value);
			map.put(name, list);
		}
	}

	public Object getValue(String name)
	{
		if (!name.contains("."))
		{
			Object value=map.get(name);
			if (value instanceof Collection)
			{
				Collection values=(Collection) value;
				if (values.isEmpty()) return null;
				return values.iterator().next();
			}
			return value;
		}
		else
		{
			String parentName=name.substring(0, name.indexOf("."));
			String childName=name.substring(name.indexOf(".")+1);
			Object value=getValue(parentName);
			if (value instanceof XPBean) return ((XPBean)value).getValue(childName);
			else return null;
		}
	}

	public Collection getValues(String name)
	{
		if (!name.contains("."))
		{
		 	Object value=map.get(name);
			if ((value==null) || (value instanceof Collection)) return (Collection) value;
			else return Collections.singletonList(value);
		}
		else
		{
			String parentName=name.substring(0, name.indexOf("."));
			String childName=name.substring(name.indexOf(".")+1);
			Collection values=getValues(parentName);
			if (values!=null)
			{
				List list=new LinkedList();
				Iterator it=values.iterator();
				while (it.hasNext())
				{
					XPBean value=(XPBean) it.next();
					Collection childBeans=value.getValues(childName);
					if (childBeans!=null) list.addAll(childBeans);
				}
				if (!list.isEmpty()) return list;
			}
			return null;
		}
	}

	public String getBasePath()
	{
		return basePath;
	}

	public List getChildren()
	{
		return list;
	}
}
