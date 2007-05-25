package com.kiwisoft.media.video;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.kiwisoft.media.photos.PhotoGallery;
import com.kiwisoft.media.photos.PhotoManager;
import com.kiwisoft.utils.gui.table.MutableSortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class PhotoGalleriesTableModel extends MutableSortableTableModel<PhotoGallery>
{
	private static final String NAME="name";

	public PhotoGalleriesTableModel()
	{
		super(new String[]{NAME}, new String[0]);
		for (PhotoGallery gallery : PhotoManager.getInstance().getGalleries()) addRow(new Row(gallery));
		sort();
	}

	public static class Row extends SortableTableRow<PhotoGallery> implements PropertyChangeListener
	{
		public Row(PhotoGallery gallery)
		{
			super(gallery);
		}

		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			if (NAME.equals(property))
				return getUserObject().getName();
			else
				return "";
		}

	}
}
