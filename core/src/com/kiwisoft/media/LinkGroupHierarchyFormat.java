package com.kiwisoft.media;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class LinkGroupHierarchyFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object object)
	{
		if (object instanceof LinkGroup)
		{
			LinkGroup linkGroup=(LinkGroup)object;
			StringBuilder text=new StringBuilder();
			while (linkGroup!=null)
			{
				if (text.length()>0) text.insert(0, " >> ");
				text.insert(0, linkGroup.getName());
				linkGroup=linkGroup.getParentGroup();
			}
			return text.toString();
		}
		return super.format(object);
	}

	@Override
	public String getIconName(Object object)
	{
		return "relatedlinkgroup";
	}
}
