/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 20, 2003
 * Time: 5:47:20 PM
 */
package com.kiwisoft.media.video;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class RecordsTableModel extends SortableTableModel<Recording>
{
	private static final String[] COLUMNS={"index", "event", "length", "language"};

	private Video video;

	public RecordsTableModel(Video video)
	{
		this.video=video;
		Iterator<Recording> it=video.getRecordings().iterator();
		while (it.hasNext())
		{
			addRow(new Row(it.next()));
		}
		sort();
	}

	public Video getVideo()
	{
		return video;
	}

	public boolean isResortable()
	{
		return false;
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	public class Row extends SortableTableRow<Recording> implements PropertyChangeListener
	{
		public Row(Recording record)
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
			Recording record=getUserObject();
			switch (column)
			{
				case 0:
					return new Integer(video.getRecordingIndex(record)+1);
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
