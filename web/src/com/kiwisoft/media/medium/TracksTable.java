package com.kiwisoft.media.medium;

import java.util.Iterator;
import java.util.ResourceBundle;

import com.kiwisoft.web.SortableWebTable;

/**
 * @author Stefan Stiller
 */
public class TracksTable extends SortableWebTable<Track>
{
	public static final String INDEX="index";
	public static final String EVENT="event";
	public static final String LENGTH="length";
	public static final String LANGUAGE="language";

	private Medium medium;

	public TracksTable(Medium medium)
	{
		super(INDEX, EVENT, LENGTH, LANGUAGE);
		this.medium=medium;
		Iterator<Track> it=medium.getTracks().iterator();
		while (it.hasNext())
		{
			addRow(new TrackRow(it.next()));
		}
		sort();
	}

	@Override
	public ResourceBundle getBundle()
	{
		return ResourceBundle.getBundle(TracksTable.class.getName());
	}

	@Override
	public boolean isResortable()
	{
		return false;
	}

	public class TrackRow extends Row<Track>
	{
		public TrackRow(Track record)
		{
			super(record);
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
			if (INDEX.equals(property)) return medium.getRecordingIndex(record)+1;
			if (EVENT.equals(property)) return record;
			if (LENGTH.equals(property)) return record.getLength();
			if (LANGUAGE.equals(property)) return record.getLanguage();
			return "";
		}
	}

}
