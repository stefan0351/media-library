package com.kiwisoft.media.download;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import java.net.URL;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class DocumentElementsNode extends GenericTreeNode<String> implements PropertyChangeListener
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
		getListeners().installPropertyChangeListener(document, this);
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
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt instanceof CollectionPropertyChangeEvent && WebDocument.ELEMENTS.equals(evt.getPropertyName()))
		{
			CollectionPropertyChangeEvent event=(CollectionPropertyChangeEvent) evt;
			if (CollectionPropertyChangeEvent.ADDED==event.getType())
			{
				addChild(new URLNode((URL)event.getElement()));
			}
		}
	}
}
