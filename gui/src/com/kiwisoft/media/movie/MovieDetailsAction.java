package com.kiwisoft.media.movie;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:17:18
 * To change this template use File | Settings | File Templates.
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
