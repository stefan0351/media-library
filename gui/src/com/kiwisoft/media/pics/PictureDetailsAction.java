package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieDetailsView;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class PictureDetailsAction extends SimpleContextAction<Picture>
{
	public PictureDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PictureDetailsView.create(getObject());
	}
}
