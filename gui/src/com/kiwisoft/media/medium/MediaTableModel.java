/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 19, 2003
 * Time: 8:31:34 PM
 */
package com.kiwisoft.media.medium;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;

import com.kiwisoft.swing.table.MutableSortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.style.ObjectStyle;
import com.kiwisoft.swing.style.StrikeThroughDecorator;
import com.kiwisoft.utils.CompoundComparable;
import com.kiwisoft.utils.StringUtils;

public class MediaTableModel extends MutableSortableTableModel<Medium>
{
	private static final String ID="id";
	private static final String NAME="name";
	private static final String TIME_LEFT="timeLeft";
	private static final String TYPE="type";
	private static final String STORAGE="storage";

	private static final Pattern STORAGE_PATTERN=Pattern.compile("(\\d+)/(\\d+)");

	private static final ObjectStyle OBSOLETE_STYLE=new ObjectStyle(new StrikeThroughDecorator(Color.RED));

	public MediaTableModel()
	{
		super(new String[]{ID, NAME, TIME_LEFT, STORAGE}, new String[]{TYPE});
	}

	public static class Row extends SortableTableRow<Medium> implements PropertyChangeListener
	{
		public Row(Medium video)
		{
			super(video);
		}

		@Override
		public ObjectStyle getCellStyle(int column, String property)
		{
			if (getUserObject().isObsolete()) return OBSOLETE_STYLE;
			return null;
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		@Override
		public Comparable getSortValue(int column, String property)
		{
			if (ID.equals(property)) return getUserObject().getUserKey();
			else if (STORAGE.equals(property))
			{
				String storage=getUserObject().getStorage();
				if (StringUtils.isEmpty(storage)) return null;
				Matcher matcher=STORAGE_PATTERN.matcher(storage);
				if (matcher.matches())
					return new CompoundComparable<Integer, Integer>(new Integer(matcher.group(1)), new Integer(matcher.group(2)));
				else
					return storage;
			}
			return super.getSortValue(column, property);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if (ID.equals(property))
				return getUserObject().getFullKey();
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
