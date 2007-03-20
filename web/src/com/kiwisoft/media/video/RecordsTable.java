package com.kiwisoft.media.video;

import java.util.Iterator;
import java.util.ResourceBundle;

import com.kiwisoft.web.SortableWebTable;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 11:43:17
 * To change this template use File | Settings | File Templates.
 */
public class RecordsTable extends SortableWebTable<Recording>
{
	public static final String INDEX="index";
	public static final String EVENT="event";
	public static final String LENGTH="length";
	public static final String LANGUAGE="language";

	private Video video;

	public RecordsTable(Video video)
	{
		super(INDEX, EVENT, LENGTH, LANGUAGE);
		this.video=video;
		Iterator<Recording> it=video.getRecordings().iterator();
		while (it.hasNext())
		{
			addRow(new RecordingRow(it.next()));
		}
		sort();
	}

	public ResourceBundle getBundle()
	{
		return ResourceBundle.getBundle(RecordsTable.class.getName());
	}

	public boolean isResortable()
	{
		return false;
	}

	public class RecordingRow extends Row<Recording>
	{
		public RecordingRow(Recording record)
		{
			super(record);
		}

		public Comparable getSortValue(int column, String property)
		{
			if (column==0) return new Integer(getUserObject().getChainPosition());
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Recording record=getUserObject();
			if (INDEX.equals(property)) return video.getRecordingIndex(record)+1;
			if (EVENT.equals(property)) return record;
			if (LENGTH.equals(property)) return record.getLength();
			if (LANGUAGE.equals(property)) return record.getLanguage();
			return "";
		}
	}

}
