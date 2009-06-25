package com.kiwisoft.media.files;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieLookup;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.InvalidDataException;

/**
 * @author Stefan Stiller
 */
public class SelectMovieView extends DetailsView
{
	public static Movie createDialog(Window owner)
	{
		SelectMovieView view=new SelectMovieView();
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK)
		{
			return view.movie;
		}
		return null;
	}

	private Movie movie;

	private LookupField<Movie> movieField;

	private SelectMovieView()
	{
		setTitle("Select Movie");
		initComponents();
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return movieField;
	}

	private void initComponents()
	{
		movieField=new LookupField<Movie>(new MovieLookup());
		movieField.setPreferredSize(new Dimension(300, movieField.getPreferredSize().height));

		setLayout(new GridBagLayout());
		add(new JLabel("Movie:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(movieField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
	}


	@Override
	public boolean apply() throws InvalidDataException
	{
		movie=movieField.getValue();
		return true;
	}
}
