/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 19, 2003
 * Time: 8:31:34 PM
 */
package com.kiwisoft.media.video;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.kiwisoft.utils.gui.table.MutableSortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class VideosTableModel extends MutableSortableTableModel<Video>
{
	private static final String ID="id";
	private static final String NAME="name";
	private static final String TIME_LEFT="timeLeft";
	private static final String TYPE="type";
	private static final String STORAGE="storage";

	public VideosTableModel(MediumType type)
	{
		super(new String[]{ID, NAME, TIME_LEFT, STORAGE}, new String[]{TYPE});
		for (Video video : VideoManager.getInstance().getVideos(type)) addRow(new Row(video));
		sort();
	}

	public static class Row extends SortableTableRow<Video> implements PropertyChangeListener
	{
		public Row(Video video)
		{
			super(video);
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

		public Object getDisplayValue(int column, String property)
		{
			if (ID.equals(property))
				return getUserObject().getUserKey();
			else if (NAME.equals(property))
				return getUserObject().getName();
			else if (TIME_LEFT.equals(property))
				return getUserObject().getRemainingLength();
			else if (TYPE.equals(property))
				return getUserObject().getType();
			else if (STORAGE.equals(property))
				return getUserObject().getStorage();
			else
				return "";
		}

	}
}
