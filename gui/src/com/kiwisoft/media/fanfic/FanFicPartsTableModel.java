package com.kiwisoft.media.fanfic;

import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 15.04.2004
 * Time: 19:08:47
 * To change this template use File | Settings | File Templates.
 */
public class FanFicPartsTableModel extends SortableTableModel
{
	private static final String[] COLUMNS={"parts"};

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	public void addPart(String part)
	{
		addRow(new Row(part));
	}

	public boolean isResortable()
	{
		return false;
	}

	private class Row extends SortableTableRow
	{
		public Row(String file)
		{
			super(file);
		}

		public String getCellFormat(int column, String property)
		{
			return "FanFicPart";
		}

		public int setValue(Object value, int column, String property)
		{
			setUserObject(value);
			return CELL_UPDATE;
		}

		public Object getDisplayValue(int column, String property)
		{
			return getUserObject();
		}

		public Class getCellClass(int col, String property)
		{
			return String.class;
		}

		public boolean isEditable(int column, String property)
		{
			return true;
		}
	}
}
