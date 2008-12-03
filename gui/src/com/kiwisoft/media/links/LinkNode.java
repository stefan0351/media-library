package com.kiwisoft.media.links;

import com.kiwisoft.media.Link;
import com.kiwisoft.swing.tree.GenericTreeNode;

/**
 * @author Stefan Stiller
 */
public class LinkNode extends GenericTreeNode<Link>
{
	public LinkNode(Link link)
	{
		super(link);
		setNameProperties(Link.NAME);
	}

	@Override
	public int getSortPriority()
	{
		return 2;
	}

	@Override
	public boolean isLeaf()
	{
		return true;
	}

	@Override
	public String getToolTip()
	{
		return getUserObject().getUrl();
	}
}
