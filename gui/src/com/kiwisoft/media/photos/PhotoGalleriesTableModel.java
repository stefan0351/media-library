package com.kiwisoft.media.photos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.kiwisoft.media.photos.PhotoGallery;
import com.kiwisoft.media.photos.PhotoManager;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.DefaultSortableTableModel;

public class PhotoGalleriesTableModel extends DefaultSortableTableModel<PhotoGallery>
{
	private static final String NAME="name";
	private static final String CREATION_DATE="creationDate";

	public PhotoGalleriesTableModel()
	{
		super(NAME, CREATION_DATE);
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

		@Override
		public String getCellFormat(int column, String property)
		{
			if (CREATION_DATE.equals(property)) return "Date only";
			return super.getCellFormat(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			if (NAME.equals(property))
				return getUserObject().getName();
			else if (CREATION_DATE.equals(property))
				return getUserObject().getCreationDate();
			else
				return "";
		}

	}
}
