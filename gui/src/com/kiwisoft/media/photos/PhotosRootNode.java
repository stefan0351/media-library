package com.kiwisoft.media.photos;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeEvent;

import java.util.Vector;

/**
 * @author Stefan Stiller
 * @since 06.11.2009
 */
public class PhotosRootNode extends GenericTreeNode<PhotoManager>
{
	public PhotosRootNode()
	{
		super(PhotoManager.getInstance());
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> treeNodes=super.loadChildren();
		for (PhotoGallery gallery : getUserObject().getRootGalleries())
		{
			treeNodes.add(new PhotoGalleryNode(gallery));
		}
		return treeNodes;
	}

	@Override
	protected void installListeners()
	{
		getListeners().addDisposable(PhotoManager.getInstance().addCollectionListener(new ChildListener()));
		super.installListeners();
	}

	private class ChildListener implements CollectionChangeListener
	{
		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (PhotoManager.GALLERIES.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionChangeEvent.ADDED)
				{
					addChild(new PhotoGalleryNode((PhotoGallery)event.getElement()));
				}
				else if (event.getType()==CollectionChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
		}
	}
}
