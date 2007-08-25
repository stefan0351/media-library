package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.movie.MoviesView;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowMoviesAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowMoviesAction(ApplicationFrame frame)
	{
		super("Movies", Icons.getIcon("movie"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new MoviesView(getObject()), true);
	}
}
