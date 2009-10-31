/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 20, 2003
 * Time: 5:47:20 PM
 */
package com.kiwisoft.media.medium;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;

public class TracksTableModel extends SortableTableModel<Track>
{
	private static final String[] COLUMNS={"index", "event", "length", "language"};

	private Medium medium;

	public TracksTableModel(Medium video)
	{
		this.medium=video;
		Iterator<Track> it=video.getTracks().iterator();
		while (it.hasNext())
		{
			addRow(new Row(it.next()));
		}
		sort();
	}

	public Medium getMedium()
	{
		return medium;
	}

	@Override
	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	public class Row extends SortableTableRow<Track> implements PropertyChangeListener
	{
		public Row(Track record)
		{
			super(record);
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		@Override
		public Comparable getSortValue(int column, String property)
		{
			if (column==0) return new Integer(getUserObject().getChainPosition());
			return super.getSortValue(column, property);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			Track record=getUserObject();
			switch (column)
			{
				case 0:
					return new Integer(medium.getRecordingIndex(record)+1);
				case 1:
					return record.getName();
				case 2:
					return new Integer(record.getLength());
				case 3:
					return record.getLanguage();
			}
			return "";
		}
	}
}
