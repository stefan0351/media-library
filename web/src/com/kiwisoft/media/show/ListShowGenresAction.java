package com.kiwisoft.media.show;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.GenreManager;
import com.kiwisoft.media.Genre;
import com.kiwisoft.utils.StringUtils;

import java.util.TreeSet;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ListShowGenresAction extends BaseAction
{
	private Set<Genre> genres;

	@Override
	public String getPageTitle()
	{
		return "Shows - Genres";
	}

	@Override
	public String execute() throws Exception
	{
		genres=new TreeSet<Genre>(StringUtils.getComparator());
		genres.addAll(GenreManager.getInstance().getGenres());
		return super.execute();
	}

	public Set<Genre> getGenres()
	{
		return genres;
	}
}
