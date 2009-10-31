package com.kiwisoft.media.download;

import java.util.Vector;
import java.net.URL;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeEvent;

public class DocumentElementsNode extends GenericTreeNode<String> implements CollectionChangeListener
{
	private WebDocument document;

	public DocumentElementsNode(WebDocument document)
	{
		super("Elements");
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

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> nodes=super.loadChildren();
		for (URL url : document.getElements()) nodes.add(new URLNode(url));
		return nodes;
	}

	@Override
	public boolean isLeaf()
	{
		return document.getElements().isEmpty();
	}

	@Override
	public String getToolTip()
	{
		return document.getURL().toString();
	}

	@Override
	public void collectionChanged(CollectionChangeEvent event)
	{
		if (WebDocument.ELEMENTS.equals(event.getPropertyName()))
		{
			if (CollectionChangeEvent.ADDED==event.getType())
			{
				addChild(new URLNode((URL)event.getElement()));
			}
		}
	}
}
