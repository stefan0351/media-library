package com.kiwisoft.media.photos;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeEvent;

import java.util.Vector;

/**
 * @author Stefan Stiller
 * @since 06.11.2009
 */
public class PhotoGalleryNode extends GenericTreeNode<PhotoGallery>
{
	public PhotoGalleryNode(PhotoGallery gallery)
	{
		super(gallery);
		setNameProperties(PhotoGallery.NAME);
	}

	@Override
	public boolean isLeaf()
	{
		return getUserObject().getChildGalleries().isEmpty();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> treeNodes=super.loadChildren();
		for (PhotoGallery gallery : getUserObject().getChildGalleries())
		{
			treeNodes.add(new PhotoGalleryNode(gallery));
		}
		return treeNodes;
	}

	@Override
	protected void installListeners()
	{
		getListeners().addDisposable(getUserObject().addCollectionListener(new ChildListener()));
		super.installListeners();
	}

	private class ChildListener implements CollectionChangeListener
	{
		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (PhotoGallery.CHILD_GALLERIES.equals(event.getPropertyName()))
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
