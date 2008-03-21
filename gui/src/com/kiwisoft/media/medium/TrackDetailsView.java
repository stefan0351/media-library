package com.kiwisoft.media.medium;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.*;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.LanguageLookup;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.MovieLookup;
import com.kiwisoft.media.movie.MovieLookupHandler;
import com.kiwisoft.media.show.EpisodeLookup;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.lookup.LookupEvent;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupSelectionListener;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;

public class TrackDetailsView extends DetailsView
{
	public static void create(Medium medium)
	{
		new DetailsFrame(new TrackDetailsView(medium)).show();
	}

	public static void create(Track track)
	{
		new DetailsFrame(new TrackDetailsView(track)).show();
	}

	private Track track;
	private Medium medium;

	// Konfigurations Panel
	private LookupField<Language> languageField;
	private LookupField<Show> showField;
	private LookupField<Episode> episodeField;
	private LookupField<Movie> movieField;
	private LookupField<TrackType> typeField;
	private JTextField eventField;
	private JTextField mediumField;
	private JCheckBox qualityField;
	private JTextField lengthField;

	private TrackDetailsView(Medium video)
	{
		setTitle("New Track");
		this.medium=video;
		createContentPanel();
		initializeData();
	}

	private TrackDetailsView(Track track)
	{
		setTitle("Track - "+track.getName());
		this.track=track;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		languageField=new LookupField<Language>(new LanguageLookup());
		typeField=new LookupField<TrackType>(new TrackTypeLookup());
		showField=new LookupField<Show>(new ShowLookup());
		episodeField=new LookupField<Episode>(new DialogEpisodeLookup());
		movieField=new LookupField<Movie>(new MovieLookup(), new MovieLookupHandler());
		eventField=new JTextField();
		mediumField=new JTextField();
		mediumField.setEditable(false);
		qualityField=new JCheckBox();
		lengthField=new JTextField();
		lengthField.setHorizontalAlignment(JTextField.TRAILING);

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 250));
		int row=0;
		add(new JLabel("Medium:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(mediumField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Language:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(languageField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Type:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(typeField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(showField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Episode:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(episodeField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Movie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(movieField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Event:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(eventField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Length:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(lengthField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("LP:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(qualityField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		showField.addSelectionListener(new ShowSelectionListener());
		movieField.addSelectionListener(new MovieSelectionListener());
	}

	public JComponent getDefaultFocusComponent()
	{
		return showField;
	}

	private void initializeData()
	{
		if (track!=null)
		{
			mediumField.setText(track.getMedium().getName());
			eventField.setText(track.getEvent());
			showField.setValue(track.getShow());
			episodeField.setValue(track.getEpisode());
			movieField.setValue(track.getMovie());
			languageField.setValue(track.getLanguage());
			lengthField.setText(Integer.toString(track.getLength()));
			qualityField.setSelected(track.isLongPlay());
			typeField.setValue(track.getType());
		}
		else if (medium!=null)
		{
			mediumField.setText(medium.getName());
			languageField.setValue(LanguageManager.getInstance().getLanguageBySymbol("de"));
			typeField.setValue(TrackType.VIDEO);
		}
	}

	public boolean apply()
	{
		String event;
		Show show;
		Episode episode;
		Movie movie;
		Language language;
		TrackType trackType;
		int length;
		boolean longPlay;
		try
		{
			event=eventField.getText();
			if (StringUtils.isEmpty(event)) event=null;
			show=showField.getValue();
			episode=episodeField.getValue();
			movie=movieField.getValue();
			language=languageField.getValue();
			trackType=typeField.getValue();
			if (trackType==null) throw new InvalidDataException("Track type must not be null!", typeField);
			try
			{
				length=Integer.parseInt(lengthField.getText());
				if (length<0 || length>500) throw new NumberFormatException();
			}
			catch (NumberFormatException e)
			{
				throw new InvalidDataException(e.getMessage(), lengthField);
			}
			longPlay=qualityField.isSelected();
		}
		catch (InvalidDataException e)
		{
			e.handle();
			return false;
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (track==null) track=medium.createTrack();
			track.setEvent(event);
			track.setShow(show);
			track.setEpisode(episode);
			track.setLongPlay(longPlay);
			track.setLength(length);
			track.setLanguage(language);
			track.setMovie(movie);
			track.setType(trackType);
			transaction.close();
			return true;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			try
			{
				transaction.rollback();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private class ShowSelectionListener implements LookupSelectionListener
	{
		public void selectionChanged(LookupEvent event)
		{
			Show show=showField.getValue();
			if (show!=null && StringUtils.isEmpty(lengthField.getText()))
				lengthField.setText(String.valueOf(show.getDefaultEpisodeLength()));
			Episode episode=episodeField.getValue();
			if (episode!=null && episode.getShow()!=show) episodeField.setValue(null);
		}
	}

	private class MovieSelectionListener implements LookupSelectionListener
	{
		public void selectionChanged(LookupEvent event)
		{
			Movie movie=movieField.getValue();
			if (movie!=null)
			{
				if (StringUtils.isEmpty(lengthField.getText())) lengthField.setText(String.valueOf(movie.getRuntime()));
				showField.setValue(movie.getShow());
			}
		}
	}

	private class DialogEpisodeLookup extends EpisodeLookup
	{
		public DialogEpisodeLookup()
		{
			super(null);
		}

		protected Show getShow()
		{
			return showField.getValue();
		}
	}
}
