package com.kiwisoft.media.download;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class GrabberProjectNode extends GenericTreeNode<GrabberProject>
{
	public GrabberProjectNode(GrabberProject project)
	{
		super(project);
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(getUserObject(), new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (GrabberProject.STATE.equals(evt.getPropertyName())) fireStructureChanged();
				else if (GrabberProject.FOLDERS.equals(evt.getPropertyName()) && evt instanceof CollectionPropertyChangeEvent)
				{
					CollectionPropertyChangeEvent event=(CollectionPropertyChangeEvent) evt;
					if (CollectionPropertyChangeEvent.ADDED==event.getType())
					{
						addChild(new FolderNode((WebFolder)event.getElement()));
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
		for (WebFolder folder : getUserObject().getFolders()) nodes.add(new FolderNode(folder));
		return nodes;
	}

	@Override
	public boolean isLeaf()
	{
		return getUserObject().getFolders().isEmpty();
	}
}
