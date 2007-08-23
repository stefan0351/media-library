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
import com.kiwisoft.utils.gui.lookup.LookupEvent;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupSelectionListener;
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
	private LookupField<Language> tfLanguage;
	private LookupField<Show> tfShow;
	private LookupField<Episode> tfEpisode;
	private LookupField<Movie> tfMovie;
	private JTextField tfEvent;
	private JTextField mediumField;
	private JCheckBox cbLongPlay;
	private JTextField tfLength;

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
		tfLanguage=new LookupField<Language>(new LanguageLookup());
		tfShow=new LookupField<Show>(new ShowLookup());
		tfEpisode=new LookupField<Episode>(new DialogEpisodeLookup());
		tfMovie=new LookupField<Movie>(new MovieLookup(), new MovieLookupHandler());
		tfEvent=new JTextField();
		mediumField=new JTextField();
		mediumField.setEditable(false);
		cbLongPlay=new JCheckBox();
		tfLength=new JTextField();
		tfLength.setHorizontalAlignment(JTextField.TRAILING);

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
		add(tfLanguage, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Episode:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfEpisode, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Movie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfMovie, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Event:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfEvent, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Length:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfLength, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("LP:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(cbLongPlay, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		tfShow.addSelectionListener(new ShowSelectionListener());
		tfMovie.addSelectionListener(new MovieSelectionListener());
	}

	public JComponent getDefaultFocusComponent()
	{
		return tfShow;
	}

	private void initializeData()
	{
		if (track!=null)
		{
			mediumField.setText(track.getMedium().getName());
			tfEvent.setText(track.getEvent());
			tfShow.setValue(track.getShow());
			tfEpisode.setValue(track.getEpisode());
			tfMovie.setValue(track.getMovie());
			tfLanguage.setValue(track.getLanguage());
			tfLength.setText(Integer.toString(track.getLength()));
			cbLongPlay.setSelected(track.isLongPlay());
		}
		else if (medium!=null)
		{
			mediumField.setText(medium.getName());
			tfLanguage.setValue(LanguageManager.getInstance().getLanguageBySymbol("de"));
		}
	}

	public boolean apply()
	{
		String event=tfEvent.getText();
		if (StringUtils.isEmpty(event)) event=null;
		Show show=tfShow.getValue();
		Episode episode=tfEpisode.getValue();
		Movie movie=tfMovie.getValue();
		Language language=tfLanguage.getValue();
		int length;
		try
		{
			length=Integer.parseInt(tfLength.getText());
			if (length<0 || length>500) throw new NumberFormatException();
		}
		catch (NumberFormatException e)
		{
			tfLength.requestFocus();
			return false;
		}
		boolean longPlay=cbLongPlay.isSelected();

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
			Show show=tfShow.getValue();
			if (show!=null && StringUtils.isEmpty(tfLength.getText()))
				tfLength.setText(String.valueOf(show.getDefaultEpisodeLength()));
			Episode episode=tfEpisode.getValue();
			if (episode!=null && episode.getShow()!=show) tfEpisode.setValue(null);
		}
	}

	private class MovieSelectionListener implements LookupSelectionListener
	{
		public void selectionChanged(LookupEvent event)
		{
			Movie movie=tfMovie.getValue();
			if (movie!=null) tfShow.setValue(movie.getShow());
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
			return tfShow.getValue();
		}
	}
}