package com.kiwisoft.media.movie;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class MovieDetailsAction extends SimpleContextAction
{
	public MovieDetailsAction()
	{
		super(Movie.class, "Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		MovieDetailsView.create((Movie)getObject());
	}
}
