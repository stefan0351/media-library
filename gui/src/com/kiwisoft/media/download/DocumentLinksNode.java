package com.kiwisoft.media.download;

import java.util.Vector;
import java.net.URL;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class DocumentLinksNode extends GenericTreeNode<String>
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
		getListeners().installPropertyChangeListener(document, new CollectionPropertyChangeAdapter()
		{
			@Override
			public void collectionChange(CollectionPropertyChangeEvent event)
			{
				if (WebDocument.LINKS.equals(event.getPropertyName()))
				{
					if (CollectionPropertyChangeEvent.ADDED==event.getType())
					{
						addChild(new URLNode((URL)event.getElement()));
					}
				}
			}
		});
		super.installListeners();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> nodes=super.loadChildren();
		for (URL url : document.getLinks()) nodes.add(new URLNode(url));
		return nodes;
	}

	@Override
	public boolean isLeaf()
	{
		return document.getLinks().isEmpty();
	}

	@Override
	public String getToolTip()
	{
		return document.getURL().toString();
	}
}
