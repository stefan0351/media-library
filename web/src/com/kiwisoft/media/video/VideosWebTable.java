package com.kiwisoft.media.video;

import java.util.Iterator;

import com.kiwisoft.utils.SortableWebTable;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 11:37:23
 * To change this template use File | Settings | File Templates.
 */
public class VideosWebTable extends SortableWebTable<Video>
{
	private static final String ID="id";
	private static final String NAME="name";
	private static final String TIME_LEFT="timeLeft";
	private static final String TYPE="type";

	public VideosWebTable(MediumType type)
	{
		super(ID, NAME, TIME_LEFT);
		Iterator it=VideoManager.getInstance().getVideos(type).iterator();
		while (it.hasNext())
		{
			Video video=(Video)it.next();
			addRow(new VideoRow(video));
		}
	}

	public static class VideoRow extends Row<Video>
	{
		public VideoRow(Video video)
		{
			super(video);
		}

		public Object getDisplayValue(int column, String property)
		{
			if (ID.equals(property))
				return getUserObject().getUserKey();
			else if (NAME.equals(property))
				return getUserObject().getName();
			else if (TIME_LEFT.equals(property))
				return new Integer(getUserObject().getRemainingLength());
			else if (TYPE.equals(property))
				return getUserObject().getType();
			else
				return "";
		}

	}

}
