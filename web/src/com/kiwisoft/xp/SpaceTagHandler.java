/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 10, 2002
 * Time: 6:59:40 PM
 */
package com.kiwisoft.xp;

import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLTagHandler;
import org.xml.sax.Attributes;

public class SpaceTagHandler implements XMLTagHandler
{
	@Override
	public String startTag(XMLContext context, String name, Attributes attributes)
	{
		String s=attributes.getValue("count");
		if (s!=null)
		{
			try
			{
				StringBuilder buffer=new StringBuilder();
				int count=Integer.parseInt(s);
				for (int i=0;i<count;i++) buffer.append("&nbsp;");
				return buffer.toString();
			}
			catch (NumberFormatException e)
			{
			}
		}
		return "&nbsp;";
	}

	@Override
	public String endTag(XMLContext context, String name)
	{
		return "";
	}
}
