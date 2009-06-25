package com.kiwisoft.media.fanfic;

import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.TableConstants;

/**
 * @author Stefan Stiller
*/
public class FanFicPartTableRow extends SortableTableRow<String>
{
	public FanFicPartTableRow(String path)
	{
		super(path);
	}

	@Override
	public String getCellFormat(int column, String property)
	{
		return "FanFicPart";
	}

	@Override
	public int setValue(Object value, int column, String property)
	{
		setUserObject((String)value);
		return TableConstants.CELL_UPDATE;
	}

	@Override
	public Object getDisplayValue(int column, String property)
	{
		return getUserObject();
	}

	@Override
	public Class getCellClass(int col, String property)
	{
		return String.class;
	}

	@Override
	public boolean isEditable(int column, String property)
	{
		return true;
	}
}
