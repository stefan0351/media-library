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

import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

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

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

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

		public Comparable getSortValue(int column, String property)
		{
			if (column==0) return new Integer(getUserObject().getChainPosition());
			return super.getSortValue(column, property);
		}

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
