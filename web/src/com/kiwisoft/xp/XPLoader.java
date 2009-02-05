/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 3, 2002
 * Time: 1:42:48 PM
 */
package com.kiwisoft.xp;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.collection.ObjectCache;
import com.kiwisoft.utils.xml.CopyingTagHandler;
import com.kiwisoft.utils.xml.ReplacingTagHandler;
import com.kiwisoft.utils.xml.XMLHandler;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLTagHandler;

public class XPLoader
{
	private static boolean USE_CACHE=true;
	private static boolean RELOAD=true;

	private static ObjectCache<File, CacheEntry> fileCache=new ObjectCache<File, CacheEntry>(100);

	private static ResourceBundle resourceBundle=ResourceBundle.getBundle("com/kiwisoft/xp/tags");
	private static Map<String, XMLTagHandler> tagHandlers;

	public static XMLObject loadXMLFile(HttpServletRequest request, File file)
	{
		CacheEntry cacheEntry=fileCache.get(file);
		if (cacheEntry==null)
		{
			return loadFile(request, file);
		}
		else
		{
			if (RELOAD)
			{
				if (file.lastModified()>cacheEntry.getLastModified())
				{
					return loadFile(request, file);
				}
				else
				{
					Iterator it=cacheEntry.getIncludes().iterator();
					while (it.hasNext())
					{
						IncludedXPBean includedBean=(IncludedXPBean) it.next();
						includedBean.reload(request);
					}
				}
			}
			return cacheEntry.getObject();
		}
	}

	private static XMLObject loadFile(HttpServletRequest request, File file)
	{
		System.out.println("Loading file: "+file.getAbsolutePath());
		XMLHandler xmlHandler=new XMLHandler();
		xmlHandler.getContext().setAttribute("request", request);
		xmlHandler.getContext().setAttribute("contextPath", request.getContextPath());
		xmlHandler.addTagMapping("*", DefaultXPBean.class);
		xmlHandler.addTagMapping("include", new IncludeFactory());

		Iterator it=getTagHandlers().keySet().iterator();
		while (it.hasNext())
		{
			String key=(String) it.next();
			xmlHandler.addTagHandler(key, (XMLTagHandler)getTagHandlers().get(key));
		}

		xmlHandler.loadFile(file);
		XMLObject rootElement=xmlHandler.getRootElement();
		if (USE_CACHE)
		{
			Set includes=(Set) xmlHandler.getContext().getAttribute("includes");
			if (includes==null) includes=Collections.EMPTY_SET;
			CacheEntry cacheEntry=new CacheEntry(rootElement, includes, file.lastModified());
			fileCache.put(file, cacheEntry);
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
			tagHandlers.put("img", new ImageTagHandler());
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

	private XPLoader()
	{
	}
}
