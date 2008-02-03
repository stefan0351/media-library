package com.kiwisoft.media.links;

import java.util.ArrayList;
import java.util.List;

import com.kiwisoft.media.Link;
import com.kiwisoft.swing.actions.ContextAction;
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
	public List<ContextAction> getContextActions()
	{
		List<ContextAction> actions=new ArrayList<ContextAction>(1);
		actions.add(new OpenLinkAction(null));
		return actions;
	}

	@Override
	public ContextAction getDoubleClickAction()
	{
		return new LinkDetailsAction();
	}
}
