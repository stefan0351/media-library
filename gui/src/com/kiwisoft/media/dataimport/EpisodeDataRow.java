package com.kiwisoft.media.dataimport;

import com.kiwisoft.swing.table.BeanTableRow;
import org.jetbrains.annotations.NonNls;

/**
 * @author Stefan Stiller
* @since 04.04.2010
*/
public class EpisodeDataRow extends BeanTableRow<EpisodeData>
{
	public static final String SELECTED="selected";

	private Integer index;
	private boolean selected;

	public EpisodeDataRow(int index, EpisodeData userObject)
	{
		super(userObject);
		this.index=index;
	}

	@Override
	public Comparable getSortValue(int column, @NonNls String property)
	{
		if (EpisodeData.KEY.equals(property)) return index;
		return super.getSortValue(column, property);
	}

	@Override
	public Object getDisplayValue(int column, @NonNls String property)
	{
		if (SELECTED.equals(property)) return selected;
		return super.getDisplayValue(column, property);
	}

	@Override
	public Class getCellClass(int column, @NonNls String property)
	{
		if (SELECTED.equals(property)) return Boolean.class;
		return super.getCellClass(column, property);
	}

	@Override
	public boolean isEditable(int column, @NonNls String property)
	{
		if (SELECTED.equals(property)) return true;
		return super.isEditable(column, property);
	}

	@Override
	public int setValue(Object value, int column, @NonNls String property)
	{
		if (SELECTED.equals(property)) selected=Boolean.TRUE.equals(value);
		return super.setValue(value, column, property);
	}

	public void setSelected(boolean selected)
	{
		if (selected!=this.selected)
		{
			this.selected=selected;
			fireRowUpdated();
		}
	}

	public boolean isSelected()
	{
		return selected;
	}
}
