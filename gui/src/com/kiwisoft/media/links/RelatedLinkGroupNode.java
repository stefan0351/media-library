package com.kiwisoft.media.links;

import java.util.Vector;
import java.util.Set;

import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.Link;
import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeEvent;

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
		return "RelatedLinkGroup";
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
