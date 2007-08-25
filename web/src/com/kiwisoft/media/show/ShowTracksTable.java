package com.kiwisoft.media.show;

import java.util.Iterator;
import java.util.ResourceBundle;

import com.kiwisoft.media.medium.Track;
import com.kiwisoft.web.SortableWebTable;
import com.kiwisoft.web.TableSortDescription;
import com.kiwisoft.web.TableConstants;

/**
 * @author Stefan Stiller
 */
public class ShowTracksTable extends SortableWebTable<Track>
{
	public static final String EVENT="event";
	public static final String MEDIUM="medium";
	public static final String STORAGE="storage";
	public static final String LANGUAGE="language";

	public ShowTracksTable(Show show)
	{
		super(EVENT, MEDIUM, STORAGE, LANGUAGE);
		initializeData(show);
	}

	private void initializeData(Show show)
	{
		Iterator it=show.getRecordings().iterator();
		while (it.hasNext()) addRow(new TrackRow((Track)it.next()));
		setSortColumn(new TableSortDescription(0, TableConstants.ASCEND));
		sort();
	}

	@Override
	public String getRendererVariant(int rowIndex, int columnIndex)
	{
		if (columnIndex==0) return "Show";
		return super.getRendererVariant(rowIndex, columnIndex);
	}

	public ResourceBundle getBundle()
	{
		return ResourceBundle.getBundle(ShowTracksTable.class.getName());
	}

	private static class TrackRow extends Row<Track>
	{
		public TrackRow(Track track)
		{
			super(track);
		}

		public Comparable getSortValue(int column, String property)
		{
			if (EVENT.equals(property))
			{
				Episode episode=getUserObject().getEpisode();
				if (episode!=null) return episode.getChainPosition();
				else return Integer.MAX_VALUE;
			}
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Track track=getUserObject();
			if (EVENT.equals(property)) return track;
			else if (MEDIUM.equals(property)) return track.getMedium();
			else if (STORAGE.equals(property)) return track.getMedium().getStorage();
			else if (LANGUAGE.equals(property)) return track.getLanguage();
			return null;
		}
	}

}
