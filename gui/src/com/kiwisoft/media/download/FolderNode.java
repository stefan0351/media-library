package com.kiwisoft.media.download;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class FolderNode extends GenericTreeNode<WebFolder>
{
	public FolderNode(WebFolder folder)
	{
		super(folder);
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(getUserObject(), new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt instanceof CollectionPropertyChangeEvent)
				{
					CollectionPropertyChangeEvent event=(CollectionPropertyChangeEvent) evt;
					if (WebFolder.SUB_FOLDERS.equals(event.getPropertyName()))
					{
						if (CollectionPropertyChangeEvent.ADDED==event.getType())
						{
							addChild(new FolderNode((WebFolder)event.getElement()));
						}
					}
					else if (WebFolder.DOCUMENTS.equals(event.getPropertyName()))
					{
						if (CollectionPropertyChangeEvent.ADDED==event.getType())
						{
							addChild(new DocumentNode((WebDocument)event.getElement()));
						}
					}
				}
			}
		});
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
}
