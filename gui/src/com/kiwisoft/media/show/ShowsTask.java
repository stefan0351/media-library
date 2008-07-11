package com.kiwisoft.media.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kiwisoft.media.Genre;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class ShowsTask extends MenuSidebarItem.Task
{
	public ShowsTask()
	{
		super("Shows");
		try
		{
			List<Genre> genres=new ArrayList<Genre>(DBLoader.getInstance().loadSet(Genre.class));
			Collections.sort(genres, StringUtils.getComparator());
			for (Genre genre : genres)
			{
				add(new ShowGenreTask(genre));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ShowsView(null));
	}
}
