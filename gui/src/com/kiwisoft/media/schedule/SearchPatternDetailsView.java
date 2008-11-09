package com.kiwisoft.media.schedule;

import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import javax.swing.*;

import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonLookup;
import com.kiwisoft.media.dataImport.SearchPattern;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieLookup;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.swing.lookup.*;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.StringUtils;

public class SearchPatternDetailsView extends DetailsView
{
	public static void create(SearchPattern pattern)
	{
		new DetailsFrame(new SearchPatternDetailsView(pattern)).show();
	}

	private SearchPattern pattern;

	// Konfigurations Panel
	private LookupField<Show> showField;
	private JRadioButton showEnabledField;
	private LookupField<Person> personField;
	private JRadioButton personEnabledField;
	private LookupField<Movie> movieField;
	private JRadioButton movieEnabledField;
	private DialogLookupField patternField;

	private SearchPatternDetailsView(SearchPattern pattern)
	{
		this.pattern=pattern;
		setTitle("Search Pattern");
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		ButtonGroup referenceGroup=new ButtonGroup();
		referenceGroup.add(showEnabledField=new JRadioButton());
		referenceGroup.add(movieEnabledField=new JRadioButton());
		referenceGroup.add(personEnabledField=new JRadioButton());

		showField=new LookupField<Show>(new ShowLookup());
		movieField=new LookupField<Movie>(new MovieLookup());
		personField=new LookupField<Person>(new PersonLookup());
		patternField=new DialogLookupField(new PatternLookup());

		getListenerList().installComponentEnabler(showEnabledField, showField);
		getListenerList().installComponentEnabler(movieEnabledField, movieField);
		getListenerList().installComponentEnabler(personEnabledField, personField);

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 150));
		int row=0;
		add(new JLabel("Reference:"), new GridBagConstraints(0, row, 3, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		row++;
		add(showEnabledField, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		add(new JLabel("Show:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		add(showField, new GridBagConstraints(2, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(movieEnabledField, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		add(new JLabel("Movie:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		add(movieField, new GridBagConstraints(2, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(personEnabledField, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		add(new JLabel("Person:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		add(personField, new GridBagConstraints(2, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Pattern:"), new GridBagConstraints(0, row, 2, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(patternField, new GridBagConstraints(2, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}

	private void initializeData()
	{
		if (pattern!=null)
		{
			IDObject reference=pattern.getReference();
			if (reference instanceof Movie)
			{
				movieEnabledField.setSelected(true);
				movieField.setValue((Movie)reference);
			}
			else
			{
				movieEnabledField.setSelected(false);
				movieField.setValue(null);
			}
			if (reference instanceof Show)
			{
				showEnabledField.setSelected(true);
				showField.setValue((Show)reference);
			}
			else
			{
				showEnabledField.setSelected(false);
				showField.setValue(null);
			}
			if (reference instanceof Person)
			{
				personEnabledField.setSelected(true);
				personField.setValue((Person)reference);
			}
			else
			{
				personEnabledField.setSelected(false);
				personField.setValue(null);
			}
			patternField.setText(pattern.getPattern());
		}
	}

	public boolean apply() throws InvalidDataException
	{
		final Movie movie=movieEnabledField.isSelected() ? movieField.getValue() : null;
		final Show show=showEnabledField.isSelected() ? showField.getValue() : null;
		final Person person=personEnabledField.isSelected() ? personField.getValue() : null;
		if (movie==null && show==null && person==null) throw new InvalidDataException("Missing reference!",
																					  movieEnabledField.isSelected() ? movieField :
																					  showEnabledField.isSelected() ? showField : personField);
		final String patternString=patternField.getText();
		if (StringUtils.isEmpty(patternString)) throw new InvalidDataException("Missig pattern!", patternField);

		return DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				if (pattern==null)
				{
					pattern=new SearchPattern();
					pattern.fireInstanceCreated();
				}
				pattern.setMovie(movie);
				pattern.setShow(show);
				pattern.setActor(person);
				pattern.setType(SearchPattern.TVTV);
				pattern.setPattern(patternString);
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(SearchPatternDetailsView.this, throwable);
			}
		});
	}

	private class PatternLookup implements DialogLookup
	{
		public void open(JTextField field)
		{
			try
			{
				if (showEnabledField.isSelected())
				{
					Show show=showField.getValue();
					if (show!=null)
					{
						String title=show.getGermanTitle();
						if (StringUtils.isEmpty(title)) title=show.getTitle();
						field.setText(URLEncoder.encode(title, "UTF-8"));
					}
				}
				else if (movieEnabledField.isSelected())
				{
					Movie movie=movieField.getValue();
					if (movie!=null)
					{
						String title=movie.getGermanTitle();
						if (StringUtils.isEmpty(title)) title=movie.getTitle();
						field.setText(URLEncoder.encode(title, "UTF-8"));
					}
				}
				else if (personEnabledField.isSelected())
				{
					Person person=personField.getValue();
					if (person!=null) field.setText(URLEncoder.encode(person.getName(), "UTF-8"));
				}
			}
			catch (UnsupportedEncodingException e)
			{
				GuiUtils.handleThrowable(SearchPatternDetailsView.this, e);
			}
		}

		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}
}
