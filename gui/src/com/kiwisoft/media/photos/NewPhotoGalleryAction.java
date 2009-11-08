package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewPhotoGalleryAction extends ContextAction
{
	private PhotoGallery parentGallery;

	public NewPhotoGalleryAction()
	{
		super("New", Icons.getIcon("add"));
	}

	@Override
	public void update(List objects)
	{
		parentGallery=null;
		if (objects==null || objects.isEmpty())
		{
			setEnabled(true);
			return;
		}
		else if (objects.size()==1)
		{
			Object o=objects.get(0);
			if (o instanceof PhotoGalleryNode)
			{
				parentGallery=((PhotoGalleryNode) o).getUserObject();
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PhotoGalleryDetailsView.createNew(parentGallery);
	}
}
