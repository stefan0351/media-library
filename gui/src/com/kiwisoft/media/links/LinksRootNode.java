package com.kiwisoft.media.links;

import java.util.Vector;

import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;
import com.kiwisoft.utils.Filter;
import com.kiwisoft.utils.FilterUtils;

/**
 * @author Stefan Stiller
 */
public class LinksRootNode extends GenericTreeNode<String>
{
	private Filter<Object> filter;

	public LinksRootNode(Filter<Object> filter)
	{
		super("Links");
		this.filter=filter;
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(LinkManager.getInstance(), new ChildListener());
		super.installListeners();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> treeNodes=super.loadChildren();
		for (LinkGroup linkGroup : FilterUtils.filterSet(LinkManager.getInstance().getRootGroups(), filter))
		{
			treeNodes.add(new LinkGroupNode(linkGroup, filter));
		}
		return treeNodes;
	}

	private class ChildListener extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (LinkManager.ROOT_GROUPS.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionPropertyChangeEvent.ADDED)
				{
					LinkGroup linkGroup=(LinkGroup) event.getElement();
					if (filter==null || filter.filter(linkGroup))
					{
						addChild(new LinkGroupNode(linkGroup, filter));
					}
				}
				else if (event.getType()==CollectionPropertyChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
		}
	}
}
