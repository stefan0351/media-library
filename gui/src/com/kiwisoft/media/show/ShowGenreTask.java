package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.show.ShowsView;
import com.kiwisoft.media.Genre;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:53:59
 * To change this template use File | Settings | File Templates.
 */
public class ShowGenreTask extends MenuSidebarItem.Task
{
	private Genre genre;

	public ShowGenreTask(Genre genre)
	{
		super(genre.getName(), IconManager.getIcon("com/kiwisoft/media/icons/show32.gif"));
		this.genre=genre;
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ShowsView(genre), true);
	}
}
