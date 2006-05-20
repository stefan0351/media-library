/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 3, 2002
 * Time: 10:37:32 AM
 */
package com.kiwisoft.xp;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLObjectFactory;
import org.xml.sax.Attributes;

public class IncludeFactory implements XMLObjectFactory
{
	public IncludeFactory()
	{
	}

	public XMLObject createElement(XMLContext context, String name, Attributes attributes)
	{
		String src=attributes.getValue("src");
		String dir=new File(context.getFileName()).getParent();
		String urlDir=new File((String)context.getAttribute("path")).getParent();
		String path=new File(dir, src).getPath();
		String urlPath=WebUtils.getPath(urlDir, src);
		XPBean bean=(XPBean)XPLoader.loadXMLFile(path,urlPath);
		IncludedXPBean include=new IncludedXPBean(bean, path, urlPath);
		Set set=(Set) context.getAttribute("includes");
		if (set==null)
		{
			set=new HashSet();
			context.setAttribute("includes", set);
		}
		set.add(include);
		return include;
	}
}
