package com.kiwisoft.media;

import java.util.List;
import java.util.ArrayList;

import com.kiwisoft.format.DefaultObjectFormat;
import com.kiwisoft.format.FormatManager;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class RelatedLinkGroupFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object object)
	{
		if (object instanceof LinkGroup)
		{
			LinkGroup linkGroup=(LinkGroup)object;
			List<String> parents=new ArrayList<String>();
			LinkGroup group=linkGroup;
			while (group.getParentGroup()!=null)
			{
				group=group.getParentGroup();
				parents.add(0, FormatManager.getInstance().format(group));
			}
			StringBuilder name=new StringBuilder(linkGroup.getName());
			name.append(" (");
			name.append(StringUtils.formatAsEnumeration(parents, " >> "));
			name.append(")");
			return name.toString();
		}
		return super.format(object);
	}

	@Override
	public String getIconName(Object object)
	{
		return "relatedlinkgroup";
	}
}
