package com.kiwisoft.media.links;

import java.util.Set;
import java.util.Vector;

import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.tree.GenericTreeNode;

/**
 * @author Stefan Stiller
 */
public class LinkGroupNode extends GenericTreeNode<LinkGroup>
{
	public LinkGroupNode(LinkGroup group)
	{
		super(group);
		setNameProperties(LinkGroup.NAME);
	}

	@Override
	public int getSortPriority()
	{
		return 1;
	}

	@Override
	protected void installListeners()
	{
		getListeners().addDisposable(getUserObject().addCollectionListener(new ChildListener()));
		super.installListeners();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Set<LinkGroup> groups=getUserObject().getSubGroups();
		Vector<GenericTreeNode> treeNodes=super.loadChildren();
		for (LinkGroup linkGroup : groups) treeNodes.add(new LinkGroupNode(linkGroup));
		Set<Link> links=getUserObject().getLinks();
		for (Link link : links) treeNodes.add(new LinkNode(link));
		Set<LinkGroup> relatedGroups=getUserObject().getRelatedGroups();
		for (LinkGroup group : relatedGroups) treeNodes.add(new RelatedLinkGroupNode(group));
		return treeNodes;
	}

	@Override
	public ContextAction getDoubleClickAction()
	{
		return new LinkDetailsAction();
	}

	private class ChildListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (LinkGroup.LINKS.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionChangeEvent.ADDED)
				{
					addChild(new LinkNode((Link)event.getElement()));
				}
				else if (event.getType()==CollectionChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
			else if (LinkGroup.SUB_GROUPS.equals(event.getPropertyName()))
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
			else if (LinkGroup.RELATED_GROUPS.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionChangeEvent.ADDED)
				{
					addChild(new RelatedLinkGroupNode((LinkGroup)event.getElement()));
				}
				else if (event.getType()==CollectionChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
		}
	}
}
