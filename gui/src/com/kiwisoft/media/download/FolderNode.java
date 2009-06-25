package com.kiwisoft.media.download;

import java.util.Vector;

import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.tree.GenericTreeNode;

public class FolderNode extends GenericTreeNode<WebFolder> implements CollectionChangeListener
{
	public FolderNode(WebFolder folder)
	{
		super(folder);
	}

	@Override
	protected void installListeners()
	{
		getListeners().addDisposable(getUserObject().addListener(this));
		super.installListeners();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		WebFolder folder=getUserObject();
		Vector<GenericTreeNode> nodes=super.loadChildren();
		for (WebFolder subFolder : folder.getFolders()) nodes.add(new FolderNode(subFolder));
		for (WebDocument document : folder.getDocuments()) nodes.add(new DocumentNode(document));
		return nodes;
	}

	public void collectionChanged(CollectionChangeEvent event)
	{
		if (WebFolder.SUB_FOLDERS.equals(event.getPropertyName()))
		{
			if (CollectionChangeEvent.ADDED==event.getType())
			{
				addChild(new FolderNode((WebFolder)event.getElement()));
			}
		}
		else if (WebFolder.DOCUMENTS.equals(event.getPropertyName()))
		{
			if (CollectionChangeEvent.ADDED==event.getType())
			{
				addChild(new DocumentNode((WebDocument)event.getElement()));
			}
		}
	}
}
