package com.kiwisoft.media.show;

import java.util.Iterator;
import java.util.ResourceBundle;

import com.kiwisoft.media.video.Recording;
import com.kiwisoft.web.SortableWebTable;
import com.kiwisoft.utils.gui.table.TableSortDescription;
import com.kiwisoft.utils.gui.table.TableConstants;

/**
 * @author Stefan Stiller
 */
public class ShowRecordsTable extends SortableWebTable<Recording>
{
	public static final String EVENT="event";
	public static final String VIDEO="video";
	public static final String STORAGE="storage";
	public static final String LANGUAGE="language";

	public ShowRecordsTable(Show show)
	{
		super(EVENT, VIDEO, STORAGE, LANGUAGE);
		initializeData(show);
	}

	private void initializeData(Show show)
	{
		Iterator it=show.getRecordings().iterator();
		while (it.hasNext()) addRow(new RecordingRow((Recording)it.next()));
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
		return ResourceBundle.getBundle(ShowRecordsTable.class.getName());
	}

	private static class RecordingRow extends Row<Recording>
	{
		public RecordingRow(Recording recording)
		{
			super(recording);
		}

		public Comparable getSortValue(int column, String property)
		{
			if (EVENT.equals(property)) return new Integer(getUserObject().getEpisode().getChainPosition());
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Recording recording=getUserObject();
			if (EVENT.equals(property)) return recording;
			else if (VIDEO.equals(property)) return recording.getVideo();
			else if (STORAGE.equals(property)) return recording.getVideo().getStorage();
			else if (LANGUAGE.equals(property)) return recording.getLanguage();
			return null;
		}
	}

}
