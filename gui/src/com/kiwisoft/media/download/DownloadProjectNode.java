package com.kiwisoft.media.download;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.tree.GenericTreeNode;

public class DownloadProjectNode extends GenericTreeNode<DownloadProject> implements PropertyChangeListener, CollectionChangeListener
{
	public DownloadProjectNode(DownloadProject project)
	{
		super(project);
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(getUserObject(), DownloadProject.STATE, this);
		getListeners().addDisposable(getUserObject().addListener(this));
		super.installListeners();
	}

	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> nodes=super.loadChildren();
		for (WebFolder folder : getUserObject().getFolders()) nodes.add(new FolderNode(folder));
		return nodes;
	}

	public boolean isLeaf()
	{
		return getUserObject().getFolders().isEmpty();
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		fireStructureChanged();
	}

	public void collectionChanged(CollectionChangeEvent event)
	{
		if (DownloadProject.FOLDERS.equals(event.getPropertyName()))
		{
			if (CollectionChangeEvent.ADDED==event.getType())
			{
				addChild(new FolderNode((WebFolder)event.getElement()));
			}
		}
	}
}
