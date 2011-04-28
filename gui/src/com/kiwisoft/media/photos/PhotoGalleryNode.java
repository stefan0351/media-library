package com.kiwisoft.media.photos;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

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
		return getUserObject().getChildren().isEmpty();
	}

	@Override
	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> treeNodes=super.loadChildren();
		for (PhotoGallery gallery : getUserObject().getChildren())
		{
			treeNodes.add(new PhotoGalleryNode(gallery));
		}
		return treeNodes;
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(getUserObject(), new ChildListener());
		super.installListeners();
	}

	private class ChildListener extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (PhotoGallery.CHILDREN.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionPropertyChangeEvent.ADDED)
				{
					addChild(new PhotoGalleryNode((PhotoGallery)event.getElement()));
				}
				else if (event.getType()==CollectionPropertyChangeEvent.REMOVED)
				{
					removeObject(event.getElement());
				}
			}
		}
	}

}
