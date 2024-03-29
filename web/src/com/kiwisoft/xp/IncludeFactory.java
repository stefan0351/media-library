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

import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLObjectFactory;
import com.kiwisoft.utils.Utils;
import org.xml.sax.Attributes;

public class IncludeFactory implements XMLObjectFactory
{
	public IncludeFactory()
	{
	}

	@Override
	public XMLObject createElement(XMLContext context, String name, Attributes attributes)
	{
		String src=attributes.getValue("src");
		File includedFile=new File(new File(context.getFileName()).getParentFile(), src);
		XPBean bean=XPLoader.loadXMLFile((HttpServletRequest)context.getAttribute("request"), includedFile);
		IncludedXPBean include=new IncludedXPBean(bean, includedFile);
		Set<IncludedXPBean> set=Utils.cast(context.getAttribute("includes"));
		if (set==null)
		{
			set=new HashSet<IncludedXPBean>();
			context.setAttribute("includes", set);
		}
		set.add(include);
		return include;
	}
}
