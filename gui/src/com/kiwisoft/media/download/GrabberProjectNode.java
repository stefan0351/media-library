package com.kiwisoft.media.download;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.tree.GenericTreeNode;

public class GrabberProjectNode extends GenericTreeNode<GrabberProject> implements PropertyChangeListener, CollectionChangeListener
{
	public GrabberProjectNode(GrabberProject project)
	{
		super(project);
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(getUserObject(), GrabberProject.STATE, this);
		getListeners().addDisposable(getUserObject().addListener(this));
		super.installListeners();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> nodes=super.loadChildren();
		for (WebFolder folder : getUserObject().getFolders()) nodes.add(new FolderNode(folder));
		return nodes;
	}

	@Override
	public boolean isLeaf()
	{
		return getUserObject().getFolders().isEmpty();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		fireStructureChanged();
	}

	@Override
	public void collectionChanged(CollectionChangeEvent event)
	{
		if (GrabberProject.FOLDERS.equals(event.getPropertyName()))
		{
			if (CollectionChangeEvent.ADDED==event.getType())
			{
				addChild(new FolderNode((WebFolder)event.getElement()));
			}
		}
	}
}
