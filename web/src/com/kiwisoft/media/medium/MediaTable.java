package com.kiwisoft.media.medium;

import java.util.Collection;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.utils.ComplexComparable;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.table.TableConstants;
import com.kiwisoft.web.SortableWebTable;
import com.kiwisoft.web.TableSortDescription;

/**
 * @author Stefan Stiller
 */
public class MediaTable extends SortableWebTable<Medium>
{
	private static final String ID="id";
	private static final String NAME="name";
	private static final String TIME_LEFT="timeLeft";
	private static final String TYPE="type";
	private static final String STORAGE="storage";

	private static final Pattern STORAGE_PATTERN=Pattern.compile("(\\d+)/(\\d+)");

	public MediaTable(int group)
	{
		super(ID, NAME, STORAGE, TIME_LEFT, TYPE);
		Collection<Medium> media;
		if (group==-1) media=MediumManager.getInstance().getAllMedia();
		else media=MediumManager.getInstance().getGroupMedia(group);
		for (Medium medium : media) addRow(new MediumRow(medium));
		setSortColumn(new TableSortDescription(0, TableConstants.ASCEND));
		sort();
	}

	public ResourceBundle getBundle()
	{
		return ResourceBundle.getBundle(MediaTable.class.getName());
	}

	@Override
	public String getRendererVariant(int rowIndex, int columnIndex)
	{
		if (NAME.equals(getColumnId(columnIndex))) return "Name";
		return super.getRendererVariant(rowIndex, columnIndex);
	}

	public static class MediumRow extends Row<Medium>
	{
		public MediumRow(Medium video)
		{
			super(video);
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
					return new ComplexComparable<Integer, Integer>(new Integer(matcher.group(1)), new Integer(matcher.group(2)));
				else
					return storage;
			}
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			if (ID.equals(property))
				return getUserObject().getFullKey();
			else if (NAME.equals(property))
				return getUserObject();
			else if (TIME_LEFT.equals(property))
				return new Integer(getUserObject().getRemainingLength());
			else if (TYPE.equals(property))
				return getUserObject().getType();
			else if (STORAGE.equals(property))
				return getUserObject().getStorage();
			else
				return "";
		}

		@Override
		public String getCellStyle(int columnIndex, String columnId)
		{
			if (getUserObject().isObsolete()) return "text-decoration:line-through";
			return super.getCellStyle(columnIndex, columnId);
		}
	}

}
