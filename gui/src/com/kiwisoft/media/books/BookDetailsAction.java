package com.kiwisoft.media.books;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class BookDetailsAction extends SimpleContextAction<Book>
{
	public BookDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		BookDetailsView.create(getObject());
	}
}
