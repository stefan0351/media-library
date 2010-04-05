package com.kiwisoft.media.show;

import com.kiwisoft.swing.table.BeanTableRow;
import org.jetbrains.annotations.NonNls;

/**
 * @author Stefan Stiller
 * @since 04.04.2010
 */
public class EpisodeRow extends BeanTableRow<Episode>
{
	public EpisodeRow(Episode episode)
	{
		super(episode);
	}

	@Override
	public Comparable getSortValue(int column, @NonNls String property)
	{
		if (Episode.USER_KEY.equals(property)) return getUserObject().getChainPosition();
		return super.getSortValue(column, property);
	}

	@Override
	public String getCellFormat(int column, String property)
	{
		if (Episode.AIRDATE.equals(property)) return "Date only";
		return super.getCellFormat(column, property);
	}

}
