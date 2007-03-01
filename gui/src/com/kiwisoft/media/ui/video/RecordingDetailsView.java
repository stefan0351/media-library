package com.kiwisoft.media.ui.video;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.*;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.ui.LanguageComboBoxRenderer;
import com.kiwisoft.media.ui.movie.MovieLookup;
import com.kiwisoft.media.ui.movie.MovieLookupHandler;
import com.kiwisoft.media.ui.show.EpisodeLookup;
import com.kiwisoft.media.ui.show.ShowLookup;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.lookup.LookupEvent;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupSelectionListener;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;

public class RecordingDetailsView extends DetailsView
{
	public static void create(Video video)
	{
		new DetailsFrame(new RecordingDetailsView(video)).show();
	}

	public static void create(Recording recording)
	{
		new DetailsFrame(new RecordingDetailsView(recording)).show();
	}

	private Recording recording;
	private Video video;

	// Konfigurations Panel
	private JComboBox cbxLanguage;
	private LookupField<Show> tfShow;
	private LookupField<Episode> tfEpisode;
	private LookupField<Movie> tfMovie;
	private JTextField tfEvent;
	private JTextField tfVideo;
	private JCheckBox cbLongPlay;
	private JTextField tfLength;

	private RecordingDetailsView(Video video)
	{
		setTitle("Neue Aufnahme");
		this.video=video;
		createContentPanel();
		initializeData();
	}

	private RecordingDetailsView(Recording recording)
	{
		setTitle("Aufnahme - "+recording.getName());
		this.recording=recording;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		cbxLanguage=new JComboBox(LanguageManager.getInstance().getLanguages().toArray());
		cbxLanguage.updateUI();
		cbxLanguage.setRenderer(new LanguageComboBoxRenderer());
		tfShow=new LookupField<Show>(new ShowLookup());
		tfEpisode=new LookupField<Episode>(new DialogEpisodeLookup());
		tfMovie=new LookupField<Movie>(new MovieLookup(), new MovieLookupHandler());
		tfEvent=new JTextField();
		tfVideo=new JTextField();
		tfVideo.setEditable(false);
		cbLongPlay=new JCheckBox();
		tfLength=new JTextField();
		tfLength.setHorizontalAlignment(JTextField.TRAILING);

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 250));
		int row=0;
		add(new JLabel("Video:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfVideo, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Sprache:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbxLanguage, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Serie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Episode:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfEpisode, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Film:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfMovie, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Ereignis:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfEvent, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Länge:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfLength, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Longplay:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
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
		if (recording!=null)
		{
			tfVideo.setText(recording.getVideo().getName());
			tfEvent.setText(recording.getEvent());
			tfShow.setValue(recording.getShow());
			tfEpisode.setValue(recording.getEpisode());
			tfMovie.setValue(recording.getMovie());
			cbxLanguage.setSelectedItem(recording.getLanguage());
			tfLength.setText(Integer.toString(recording.getLength()));
			cbLongPlay.setSelected(recording.isLongPlay());
		}
		else if (video!=null)
		{
			tfVideo.setText(video.getName());
			cbxLanguage.setSelectedItem(LanguageManager.getInstance().getLanguageBySymbol("de"));
		}
	}

	public boolean apply()
	{
		String event=tfEvent.getText();
		if (StringUtils.isEmpty(event)) event=null;
		Show show=tfShow.getValue();
		Episode episode=tfEpisode.getValue();
		Movie movie=tfMovie.getValue();
		Language language=(Language)cbxLanguage.getSelectedItem();
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
			if (recording==null) recording=video.createRecording();
			recording.setEvent(event);
			recording.setShow(show);
			recording.setEpisode(episode);
			recording.setLongPlay(longPlay);
			recording.setLength(length);
			recording.setLanguage(language);
			recording.setMovie(movie);
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
