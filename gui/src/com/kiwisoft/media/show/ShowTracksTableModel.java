package com.kiwisoft.media.show;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.Language;

/**
 * @author Stefan Stiller
 */
public class ShowTracksTableModel extends SortableTableModel<Track>
{
	private static final String[] COLUMNS={"key", "name", "medium", "language"};

	public ShowTracksTableModel(Show show)
	{
		initializeData(show);
	}

	private void initializeData(Show show)
	{
		for (Track track : show.getRecordings()) addRow(new Row(track));
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	private static class Row extends SortableTableRow<Track> implements PropertyChangeListener
	{
		public Row(Track track)
		{
			super(track);
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
			Track track=getUserObject();
			Episode episode=track.getEpisode();
			switch (column)
			{
				case 0: // key
					if (episode!=null) return episode.getUserKey();
					else return null;
				case 1: // name
					if (episode!=null) return episode.getTitle(track.getLanguage());
					return track.getEvent();
				case 2: // medium
					try
					{
						return track.getMedium().getFullKey();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				case 3: // language
					return track.getLanguage();
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
