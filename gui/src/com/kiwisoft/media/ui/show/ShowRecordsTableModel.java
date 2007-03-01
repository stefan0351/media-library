package com.kiwisoft.media.ui.show;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 19.05.2004
 * Time: 21:56:27
 * To change this template use File | Settings | File Templates.
 */
public class ShowRecordsTableModel extends SortableTableModel
{
	private static final String[] COLUMNS={"key", "name", "video", "language"};

	public ShowRecordsTableModel(Show show)
	{
		initializeData(show);
	}

	private void initializeData(Show show)
	{
		Iterator it=show.getRecordings().iterator();
		while (it.hasNext()) addRow(new Row((Recording)it.next()));
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	private static class Row extends SortableTableRow implements PropertyChangeListener
	{
		public Row(Recording recording)
		{
			super(recording);
		}

		public void installListener()
		{
			((Recording)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Recording)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Comparable getSortValue(int column, String property)
		{
			if (column==0) return new Integer(((Recording)getUserObject()).getEpisode().getChainPosition());
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Recording recording=(Recording)getUserObject();
			Episode episode=recording.getEpisode();
			switch (column)
			{
				case 0: // key
					if (episode!=null) return episode.getUserKey();
					else return null;
				case 1: // name
					if (episode!=null) return episode.getName(recording.getLanguage());
					return recording.getEvent();
				case 2: // video
					try
					{
						return recording.getVideo().getUserKey();
					}
					catch (Exception e)
					{
						System.out.println("ShowRecordsTableModel$Row.getDisplayValue: recording.getId() = "+recording.getId());
						e.printStackTrace();
					}
				case 3: // language
					return recording.getLanguage();
			}
			return null;
		}

		public Class getCellClass(int col, String property)
		{
			if (col==3) return Language.class;
			return super.getCellClass(col, property);
		}
	}
}
