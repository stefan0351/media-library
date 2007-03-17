package com.kiwisoft.media.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kiwisoft.media.Genre;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:53:59
 * To change this template use File | Settings | File Templates.
 */
public class ShowsTask extends MenuSidebarItem.Task
{
	public ShowsTask()
	{
		super("Shows");
		List<Genre> genres=new ArrayList<Genre>(DBLoader.getInstance().loadSet(Genre.class));
		Collections.sort(genres, StringUtils.getComparator());
		for (Genre genre : genres)
		{
			add(new ShowGenreTask(genre));
		}
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ShowsView(null), true);
	}
}
