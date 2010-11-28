/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 3, 2002
 * Time: 10:19:33 AM
 */
package com.kiwisoft.xp;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.media.MediaConfiguration;

public class DefaultXPBean implements XPBean
{
	private final static List<String> SOURCE_ATTRIBUTES=Arrays.asList("image.preview",
																	  "image.source",
																	  "theme.source",
																	  "song.source",
																	  "fanfic.next");
	private Map<String, Object> map;
	private List<Object> list;
	private String name;
	private String content;

	@SuppressWarnings({"UnusedDeclaration"})
	public DefaultXPBean(XMLContext context, String name)
	{
		this.name=name;
		this.map=new HashMap<String, Object>();
		this.list=new ArrayList<Object>();
	}

	@Override
	public void setXMLAttribute(XMLContext context, String uri, String name, String value)
	{
		if (SOURCE_ATTRIBUTES.contains((this.name+"."+name).toLowerCase()))
		{
			File file=new File(new File(context.getFileName()).getParentFile(), value);
			String contextPath=(String)context.getAttribute("contextPath");
			putValue(name, (contextPath!=null ? contextPath+"/" : "")+"res/"+FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath()).replace('\\', '/'));
		}
		putValue(name, value);
	}

	@Override
	public void setXMLReference(XMLContext context, String uri, String name, Object value)
	{
		putValue(name, value);
	}

	@Override
	public void setXMLContent(XMLContext context, String value)
	{
		content=value;
	}

	@Override
	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof XPBean)
		{
			XPBean bean=(XPBean) element;
			putValue(bean.getName(), element);
		}
		list.add(element);
	}

	@Override
	public String toString()
	{
		return content;
	}

	@Override
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

	@Override
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

	@Override
	public String getString(String name)
	{
		Object value=getValue(name);
		if (value!=null) return value.toString();
		return null;
	}

	@Override
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

	@Override
	public List getChildren()
	{
		return list;
	}
}
