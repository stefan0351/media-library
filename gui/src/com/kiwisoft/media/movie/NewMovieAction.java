package com.kiwisoft.media.movie;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.media.show.Show;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 10.03.2007
 * Time: 21:51:48
 * To change this template use File | Settings | File Templates.
 */
public class NewMovieAction extends ContextAction<Movie>
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
