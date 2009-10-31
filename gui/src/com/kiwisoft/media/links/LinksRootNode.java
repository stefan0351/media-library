package com.kiwisoft.media.links;

import java.util.Vector;

import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.swing.tree.GenericTreeNode;

/**
 * @author Stefan Stiller
 */
public class LinksRootNode extends GenericTreeNode<String>
{
	public LinksRootNode()
	{
		super("Links");
	}

	@Override
	protected void installListeners()
	{
		getListeners().addDisposable(LinkManager.getInstance().addCollectionListener(new ChildListener()));
		super.installListeners();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> treeNodes=super.loadChildren();
		for (LinkGroup linkGroup : LinkManager.getInstance().getRootGroups())
		{
			treeNodes.add(new LinkGroupNode(linkGroup));
		}
		return treeNodes;
	}

	private class ChildListener implements CollectionChangeListener
	{
		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (LinkManager.ROOT_GROUPS.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionChangeEvent.ADDED)
				{
					addChild(new LinkGroupNode((LinkGroup)event.getElement()));
				}
				else if (event.getType()==CollectionChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
		}
	}
}
