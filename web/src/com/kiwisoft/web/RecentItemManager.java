package com.kiwisoft.web;

import com.kiwisoft.app.Application;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.xml.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class RecentItemManager
{
	private static RecentItemManager instance;

	public static RecentItemManager getInstance()
	{
		synchronized (RecentItemManager.class)
		{
			if (instance==null)
			{
				instance=new RecentItemManager();
				instance.load();
			}
		}
		return instance;
	}

	private List<RecentItem> recentItems=new ArrayList<RecentItem>();

	public void addItem(RecentItem item)
	{
		synchronized (this)
		{
			recentItems.remove(item);
			recentItems.add(0, item);
			while (recentItems.size()>10) recentItems.remove(10);
			try
			{
				save();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public List<RecentItem> getRecentItems()
	{
		return Collections.unmodifiableList(recentItems);
	}

	private File getFile()
	{
		String applicationName=Application.getInstance().getName();
		return new File(System.getProperty("user.home")+File.separator+".kiwisoft"+File.separator+applicationName+File.separator+"recent.xml"); // NON-NLS
	}

	private void load()
	{
		File file=getFile();
		if (file.exists())
		{
			recentItems.clear();
			XMLHandler xmlHandler=new XMLHandler();
			xmlHandler.addTagMapping("recentItems", new XMLAdapter()
			{
				@Override
				public void addXMLElement(XMLContext context, XMLObject element)
				{
					if (element instanceof RecentItemAdapter)
					{
						RecentItemAdapter adapter=(RecentItemAdapter) element;
						try
						{
							recentItems.add(adapter.getItem());
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			xmlHandler.addTagMapping("recentItem", RecentItemAdapter.class);
			xmlHandler.addTagMapping("property", XMLPropertyAdapter.class);
			xmlHandler.loadFile(file);
		}
	}

	public void save() throws IOException
	{
		File file=getFile();
		file.getParentFile().mkdirs();
		XMLWriter xmlWriter=new XMLWriter(new FileWriter(file), null);
		xmlWriter.start();
		xmlWriter.startElement("recentItems");
		for (RecentItem recentItem : recentItems)
		{
			xmlWriter.startElement("recentItem");
			xmlWriter.setAttribute("class", recentItem.getClass().getName());
			Map<String, String> properties=recentItem.getProperties();
			for (Map.Entry<String, String> entry : properties.entrySet())
			{
				xmlWriter.startElement("property");
				xmlWriter.setAttribute("name", entry.getKey());
				xmlWriter.setAttribute("value", entry.getValue());
				xmlWriter.closeElement("property");
			}
			xmlWriter.closeElement("recentItem");
		}
		xmlWriter.closeElement("recentItems");
		xmlWriter.close();
	}

	public static class RecentItemAdapter extends XMLAdapter
	{
		private String clazz;
		private Map<String, String> properties=new HashMap<String, String>();

		public RecentItemAdapter(XMLContext context, String name)
		{
			super(context, name);
		}

		@Override
		public void setXMLAttribute(XMLContext context, String uri, String name, String value)
		{
			if ("class".equalsIgnoreCase(name)) clazz=value;
		}

		@Override
		public void addXMLElement(XMLContext context, XMLObject element)
		{
			if (element instanceof XMLPropertyAdapter)
			{
				XMLPropertyAdapter propertyAdapter=(XMLPropertyAdapter) element;
				properties.put(propertyAdapter.getName(), propertyAdapter.getValue());
			}
		}

		public RecentItem getItem() throws Exception
		{
			Class<? extends RecentItem> aClass=Utils.cast(Class.forName(clazz));
			RecentItem recentItem=aClass.newInstance();
			recentItem.setProperties(properties);
			return recentItem;
		}
	}
}
