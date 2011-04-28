package com.kiwisoft.media.links;

import java.util.Set;
import java.util.Vector;

import com.kiwisoft.media.Link;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;
import com.kiwisoft.utils.Filter;
import com.kiwisoft.utils.FilterUtils;

/**
 * @author Stefan Stiller
 */
public class LinkGroupNode extends GenericTreeNode<LinkGroup>
{
	private Filter<Object> filter;

	public LinkGroupNode(LinkGroup group, Filter<Object> filter)
	{
		super(group);
		this.filter=filter;
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
		getListeners().installPropertyChangeListener(getUserObject(), new ChildListener());
		super.installListeners();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Set<LinkGroup> groups=FilterUtils.filterSet(getUserObject().getSubGroups(), filter);
		Vector<GenericTreeNode> treeNodes=super.loadChildren();
		for (LinkGroup linkGroup : groups) treeNodes.add(new LinkGroupNode(linkGroup, filter));
		Set<Link> links=FilterUtils.filterSet(getUserObject().getLinks(), filter);
		for (Link link : links) treeNodes.add(new LinkNode(link));
		Set<LinkGroup> relatedGroups=FilterUtils.filterSet(getUserObject().getRelatedGroups(), filter);
		for (LinkGroup group : relatedGroups) treeNodes.add(new RelatedLinkGroupNode(group));
		return treeNodes;
	}

	private class ChildListener extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (LinkGroup.LINKS.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionPropertyChangeEvent.ADDED)
				{
					Link link=(Link) event.getElement();
					if (filter==null || filter.filter(link)) addChild(new LinkNode(link));
				}
				else if (event.getType()==CollectionPropertyChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
			else if (LinkGroup.SUB_GROUPS.equals(event.getPropertyName()))
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
			else if (LinkGroup.RELATED_GROUPS.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionPropertyChangeEvent.ADDED)
				{
					LinkGroup linkGroup=(LinkGroup) event.getElement();
					if (filter==null || filter.filter(linkGroup)) addChild(new RelatedLinkGroupNode(linkGroup));
				}
				else if (event.getType()==CollectionPropertyChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
		}
	}
}
