/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 19, 2003
 * Time: 8:31:34 PM
 */
package com.kiwisoft.media.ui.video;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.VideoManager;
import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.MutableSortableTableModel;

public class VideosTableModel extends MutableSortableTableModel
{
	private static final String ID="id";
	private static final String NAME="name";
	private static final String TIME_LEFT="timeLeft";
	private static final String TYPE="type";

	public VideosTableModel(MediumType type)
	{
		super(new String[]{ID, NAME, TIME_LEFT}, new String[]{TYPE});
		Iterator it=VideoManager.getInstance().getVideos(type).iterator();
		while (it.hasNext())
		{
			Video video=(Video)it.next();
			addRow(new Row(video));
		}
		sort();
	}

	public static class Row extends SortableTableRow implements PropertyChangeListener
	{
		public Row(Video video)
		{
			super(video);
		}

		public void installListener()
		{
			((Video)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Video)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			if (ID.equals(property))
				return ((Video)getUserObject()).getUserKey();
			else if (NAME.equals(property))
				return ((Video)getUserObject()).getName();
			else if (TIME_LEFT.equals(property))
				return new Integer(((Video)getUserObject()).getRemainingLength());
			else if (TYPE.equals(property))
				return ((Video)getUserObject()).getType();
			else
				return "";
		}

	}
}
