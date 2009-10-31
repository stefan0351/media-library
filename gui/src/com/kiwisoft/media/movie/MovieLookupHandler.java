package com.kiwisoft.media.movie;

import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupHandler;

import javax.swing.*;

/**
 * @author Stefan Stiller
 */
public class MovieLookupHandler implements LookupHandler<Movie>
{
	@Override
	public boolean isCreateAllowed()
	{
		return true;
}

	@Override
	public Movie createObject(LookupField<Movie> lookupField)
	{
		return MovieDetailsView.createDialog(SwingUtilities.getWindowAncestor(lookupField), lookupField.getText());
	}

	@Override
	public boolean isEditAllowed()
	{
		return false;
	}

	@Override
	public void editObject(LookupField<Movie> lookupField, Movie value)
	{
	}
}

