package com.kiwisoft.media.fanfic;

import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConstants;

/**
 * @author Stefan Stiller
*/
public class FanFicPartTableRow extends SortableTableRow<String>
{
	public FanFicPartTableRow(String path)
	{
		super(path);
	}

	public String getCellFormat(int column, String property)
	{
		return "FanFicPart";
	}

	public int setValue(Object value, int column, String property)
	{
		setUserObject((String)value);
		return TableConstants.CELL_UPDATE;
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
