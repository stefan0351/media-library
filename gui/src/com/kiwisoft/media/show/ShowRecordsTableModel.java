package com.kiwisoft.media.show;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.Language;

/**
 * @author Stefan Stiller
 */
public class ShowRecordsTableModel extends SortableTableModel<Recording>
{
	private static final String[] COLUMNS={"key", "name", "video", "language"};

	public ShowRecordsTableModel(Show show)
	{
		initializeData(show);
	}

	private void initializeData(Show show)
	{
		for (Recording recording : show.getRecordings()) addRow(new Row(recording));
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	private static class Row extends SortableTableRow<Recording> implements PropertyChangeListener
	{
		public Row(Recording recording)
		{
			super(recording);
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
			if (column==0)
			{
				Episode episode=getUserObject().getEpisode();
				if (episode!=null) return new Integer(episode.getChainPosition());
				else return Integer.MAX_VALUE;
			}
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Recording recording=getUserObject();
			Episode episode=recording.getEpisode();
			switch (column)
			{
				case 0: // key
					if (episode!=null) return episode.getUserKey();
					else return null;
				case 1: // name
					if (episode!=null) return episode.getTitle(recording.getLanguage());
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
