package com.kiwisoft.media.video;

import java.util.Iterator;

import com.kiwisoft.utils.SortableWebTable;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 11:43:17
 * To change this template use File | Settings | File Templates.
 */
public class RecordsWebTable extends SortableWebTable<Recording>
{
	private Video video;

	public RecordsWebTable(Video video)
	{
		super("index", "event", "length", "language");
		this.video=video;
		Iterator<Recording> it=video.getRecordings().iterator();
		while (it.hasNext())
		{
			addRow(new RecordingRow(it.next()));
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
