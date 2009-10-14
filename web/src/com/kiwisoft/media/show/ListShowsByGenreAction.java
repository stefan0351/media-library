package com.kiwisoft.media.show;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.Genre;
import com.kiwisoft.media.GenreManager;
import com.kiwisoft.utils.StringUtils;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ListShowsByGenreAction extends BaseAction
{
	private Long genreId;
	private SortedSet<Show> shows;
	private Genre genre;

	@Override
	public String getPageTitle()
	{
		return "Shows";
	}

	@Override
	public String execute() throws Exception
	{
		genre=GenreManager.getInstance().getGenre(genreId);
		shows=new TreeSet<Show>(StringUtils.getComparator());
		shows.addAll(genre.getShows());
		return super.execute();
	}

	public Long getGenreId()
	{
		return genreId;
	}

	public void setGenreId(Long genreId)
	{
		this.genreId=genreId;
	}

	public Genre getGenre()
	{
		return genre;
	}

	public Set<Show> getShows()
	{
		return shows;
	}
}
