package com.kiwisoft.media.links;

import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.swing.tree.GenericTreeNode;

/**
 * @author Stefan Stiller
 */
public class RelatedLinkGroupNode extends GenericTreeNode<LinkGroup>
{
	public RelatedLinkGroupNode(LinkGroup group)
	{
		super(group);
		setNameProperties(LinkGroup.NAME);
	}

	@Override
	public String getFormatVariant()
	{
		return "hierarchy";
	}

	@Override
	public int getSortPriority()
	{
		return 3;
	}

	@Override
	public boolean isLeaf()
	{
		return true;
	}
}
