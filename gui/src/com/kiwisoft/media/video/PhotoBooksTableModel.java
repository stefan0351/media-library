package com.kiwisoft.media.video;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.kiwisoft.media.photos.PhotoBook;
import com.kiwisoft.media.photos.PhotoManager;
import com.kiwisoft.utils.gui.table.MutableSortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class PhotoBooksTableModel extends MutableSortableTableModel<PhotoBook>
{
	private static final String NAME="name";

	public PhotoBooksTableModel()
	{
		super(new String[]{NAME}, new String[0]);
		for (PhotoBook book : PhotoManager.getInstance().getBooks()) addRow(new Row(book));
		sort();
	}

	public static class Row extends SortableTableRow<PhotoBook> implements PropertyChangeListener
	{
		public Row(PhotoBook book)
		{
			super(book);
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
