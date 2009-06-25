package com.kiwisoft.media.show;

import com.kiwisoft.media.Genre;
import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class ShowGenreTask extends MenuSidebarItem.Task
{
	private Genre genre;

	public ShowGenreTask(Genre genre)
	{
		super(genre.getName());
		this.genre=genre;
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ShowsView(genre));
	}
}
