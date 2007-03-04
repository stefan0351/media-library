package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.movie.MoviesView;
import com.kiwisoft.media.show.Show;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:45:41
 * To change this template use File | Settings | File Templates.
 */
public class ShowMoviesAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowMoviesAction(ApplicationFrame frame)
	{
		super("Filme");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new MoviesView(getObject()), true);
	}
}
