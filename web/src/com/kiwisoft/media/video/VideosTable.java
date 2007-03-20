package com.kiwisoft.media.video;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.utils.ComplexComparable;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.table.TableSortDescription;
import com.kiwisoft.utils.gui.table.TableConstants;
import com.kiwisoft.web.SortableWebTable;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 11:37:23
 * To change this template use File | Settings | File Templates.
 */
public class VideosTable extends SortableWebTable<Video>
{
	private static final String ID="id";
	private static final String NAME="name";
	private static final String TIME_LEFT="timeLeft";
	private static final String TYPE="type";
	private static final String STORAGE="storage";

	private static final Pattern STORAGE_PATTERN=Pattern.compile("(\\d+)/(\\d+)");

	public VideosTable(MediumType type)
	{
		super(ID, NAME, STORAGE, TIME_LEFT, TYPE);
		Iterator it=VideoManager.getInstance().getVideos(type).iterator();
		while (it.hasNext())
		{
			Video video=(Video)it.next();
			addRow(new VideoRow(video));
		}
		setSortColumn(new TableSortDescription(0, TableConstants.ASCEND));
		sort();
	}

	public ResourceBundle getBundle()
	{
		return ResourceBundle.getBundle(VideosTable.class.getName());
	}

	@Override
	public String getRendererVariant(int rowIndex, int columnIndex)
	{
		if (NAME.equals(getColumnId(columnIndex))) return "Name";
		return super.getRendererVariant(rowIndex, columnIndex);
	}

	public static class VideoRow extends Row<Video>
	{
		public VideoRow(Video video)
		{
			super(video);
		}

		@Override
		public Comparable getSortValue(int column, String property)
		{
			if (ID.equals(property))
			{
				String key=getUserObject().getUserKey();
				if (StringUtils.isEmpty(key)) return null;
				Matcher matcher=VideoManager.getInstance().getKeyPattern().matcher(key);
				if (matcher.matches())
					return new ComplexComparable<String, Integer>(matcher.group(1), new Integer(matcher.group(2)));
				else
					return new ComplexComparable<String, Integer>(key, null);
			}
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
				return getUserObject().getUserKey();
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

	}

}
