package com.kiwisoft.media.show;

import java.util.Iterator;

import com.kiwisoft.media.video.Recording;
import com.kiwisoft.utils.SortableWebTable;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 11:13:53
 * To change this template use File | Settings | File Templates.
 */
public class ShowRecordsWebTable extends SortableWebTable<Recording>
{
	public ShowRecordsWebTable(Show show)
	{
		super("key", "name", "video", "language");
		initializeData(show);
	}

	private void initializeData(Show show)
	{
		Iterator it=show.getRecordings().iterator();
		while (it.hasNext()) addRow(new RecordingRow((Recording)it.next()));
	}

	private static class RecordingRow extends Row<Recording>
	{
		public RecordingRow(Recording recording)
		{
			super(recording);
		}

		public Comparable getSortValue(int column, String property)
		{
			if (column==0) return new Integer(getUserObject().getEpisode().getChainPosition());
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
	}

}
