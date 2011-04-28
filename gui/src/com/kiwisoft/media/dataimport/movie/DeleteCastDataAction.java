package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.media.dataimport.CastData;
import com.kiwisoft.media.dataimport.MovieData;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.Utils;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 26.02.11
 */
public class DeleteCastDataAction extends MultiContextAction
{
	private MovieData movieData;

	public DeleteCastDataAction(MovieData movieData)
	{
		super(CastData.class, "Delete", Icons.getIcon("delete"));
		this.movieData=movieData;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<CastData> castList=Utils.cast(getObjects());
		for (CastData castData : castList)
		{
			movieData.removeCast(castData);
		}
	}
}
