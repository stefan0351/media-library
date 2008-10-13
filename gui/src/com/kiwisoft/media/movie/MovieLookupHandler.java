package com.kiwisoft.media.movie;

import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupHandler;
import com.kiwisoft.swing.GuiUtils;

/**
 * @author Stefan Stiller
 */
public class MovieLookupHandler implements LookupHandler<Movie>
{
	public boolean isCreateAllowed()
	{
		return true;
}

	public Movie createObject(LookupField<Movie> lookupField)
	{
		return MovieDetailsView.createDialog(GuiUtils.getWindow(lookupField), lookupField.getText());
	}

	public boolean isEditAllowed()
	{
		return false;
	}

	public void editObject(LookupField<Movie> lookupField, Movie value)
	{
	}
}

