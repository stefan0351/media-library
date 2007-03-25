package com.kiwisoft.media.movie;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class MovieDetailsAction extends SimpleContextAction<Movie>
{
	public MovieDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		MovieDetailsView.create(getObject());
	}
}
