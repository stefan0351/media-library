package com.kiwisoft.media.download;

import java.util.Vector;
import java.net.URL;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeEvent;

public class DocumentLinksNode extends GenericTreeNode<String> implements CollectionChangeListener
{
	private WebDocument document;

	public DocumentLinksNode(WebDocument document)
	{
		super("Links");
		this.document=document;
	}

	@Override
	public int getSortPriority()
	{
		return 1;
	}

	@Override
	protected void installListeners()
	{
		getListeners().addDisposable(document.addCollectionListener(this));
		super.installListeners();
	}

	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> nodes=super.loadChildren();
		for (URL url : document.getLinks()) nodes.add(new URLNode(url));
		return nodes;
	}

	public boolean isLeaf()
	{
		return document.getLinks().isEmpty();
	}

	@Override
	public String getToolTip()
	{
		return document.getURL().toString();
	}

	public void collectionChanged(CollectionChangeEvent event)
	{
		if (WebDocument.LINKS.equals(event.getPropertyName()))
		{
			if (CollectionChangeEvent.ADDED==event.getType())
			{
				addChild(new URLNode((URL)event.getElement()));
			}
		}
	}
}
