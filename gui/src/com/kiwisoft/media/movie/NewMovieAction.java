package com.kiwisoft.media.movie;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.show.Show;

/**
 * @author Stefan Stiller
 */
public class NewMovieAction extends ContextAction
{
	private Show show;

	public NewMovieAction(Show show)
	{
		super("New", Icons.getIcon("add"));
		this.show=show;
	}

	public void actionPerformed(ActionEvent e)
	{
		MovieDetailsView.create(show);
	}
}
