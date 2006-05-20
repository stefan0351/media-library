/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 3, 2002
 * Time: 1:42:48 PM
 */
package com.kiwisoft.xp;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.kiwisoft.utils.ObjectCache;
import com.kiwisoft.utils.xml.CopyingTagHandler;
import com.kiwisoft.utils.xml.ReplacingTagHandler;
import com.kiwisoft.utils.xml.XMLHandler;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLTagHandler;

public class XPLoader
{
	private static boolean USE_CACHE=true;
	private static boolean RELOAD=true;

	private static ObjectCache<String, CacheEntry> fileCache=new ObjectCache<String, CacheEntry>(100);

	private static ResourceBundle resourceBundle=ResourceBundle.getBundle("com/kiwisoft/xp/tags");
	private static Map<String, XMLTagHandler> tagHandlers;

	public static XMLObject loadXMLFile(String absolutPath, String path)
	{
		absolutPath=getNormalizedPath(absolutPath);
		CacheEntry cacheEntry=fileCache.get(absolutPath);
		if (cacheEntry==null)
		{
			return loadFile(absolutPath, path);
		}
		else
		{
			if (RELOAD)
			{
				if (new File(absolutPath).lastModified()>cacheEntry.getLastModified())
				{
					return loadFile(absolutPath, path);
				}
				else
				{
					Iterator it=cacheEntry.getIncludes().iterator();
					while (it.hasNext())
					{
						IncludedXPBean includedBean=(IncludedXPBean) it.next();
						includedBean.reload();
					}
				}
			}
			return cacheEntry.getObject();
		}
	}

	private static XMLObject loadFile(String absolutPath, String path)
	{
		System.out.println("Loading file: "+path);
		XMLHandler xmlHandler=new XMLHandler();
		xmlHandler.getContext().setAttribute("path", path);
		xmlHandler.addTagMapping("*", DefaultXPBean.class);
		xmlHandler.addTagMapping("include", new IncludeFactory());

		Iterator it=getTagHandlers().keySet().iterator();
		while (it.hasNext())
		{
			String key=(String) it.next();
			xmlHandler.addTagHandler(key, (XMLTagHandler)getTagHandlers().get(key));
		}

		xmlHandler.loadFile(absolutPath);
		XMLObject rootElement=xmlHandler.getRootElement();
		if (USE_CACHE)
		{
			Set includes=(Set) xmlHandler.getContext().getAttribute("includes");
			if (includes==null) includes=Collections.EMPTY_SET;
			CacheEntry cacheEntry=new CacheEntry(rootElement, includes, new File(absolutPath).lastModified());
			fileCache.put(absolutPath, cacheEntry);
		}
		return rootElement;
	}

	private static Map getTagHandlers()
	{
		if (tagHandlers==null)
		{
			tagHandlers=new HashMap<String, XMLTagHandler>();
			Enumeration<String> e=resourceBundle.getKeys();
			while (e.hasMoreElements())
			{
				String key=e.nextElement();
				String value=resourceBundle.getString(key);
				if ("copy".equals(value))
					tagHandlers.put(key, CopyingTagHandler.getInstance());
				else if ("replace".equals(value))
				{
					tagHandlers.put(key, new ReplacingTagHandler(
					    resourceBundle.getString(key+".start"), resourceBundle.getString(key+".end")));
				}
			}
			tagHandlers.put("space", new SpaceTagHandler());
			tagHandlers.put("link", new LinkTagHandler());
		}
		return tagHandlers;
	}

	private static class CacheEntry
	{
		private XMLObject object;
		private Set includes;
		private long lastModified;

		public CacheEntry(XMLObject object, Set includes, long lastModified)
		{
			this.object=object;
			this.includes=includes;
			this.lastModified=lastModified;
		}

		public XMLObject getObject()
		{
			return object;
		}

		public long getLastModified()
		{
			return lastModified;
		}

		public Set getIncludes()
		{
			return includes;
		}
	}

	private static String getNormalizedPath(String realPath)
	{
		try
		{
			return new File(realPath).getCanonicalPath();
		}
		catch (IOException e)
		{
			return realPath;
		}
	}

	private XPLoader()
	{
	}
}
