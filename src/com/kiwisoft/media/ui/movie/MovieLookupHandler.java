package com.kiwisoft.media.ui.movie;

import java.awt.Window;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.ui.movie.MovieDetailsView;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2005
 * Time: 19:23:38
 * To change this template use File | Settings | File Templates.
 */
public class MovieLookupHandler implements LookupHandler<Movie>
{
	public boolean isCreateAllowed()
	{
		return true;
}

	public Movie createObject(LookupField<Movie> lookupField)
	{
		return MovieDetailsView.createDialog((Window)lookupField.getTopLevelAncestor(), lookupField.getText());
	}

	public boolean isEditAllowed()
	{
		return false;
	}

	public void editObject(Movie movie)
	{
	}
}

