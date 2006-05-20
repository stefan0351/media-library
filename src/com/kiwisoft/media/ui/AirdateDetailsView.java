package com.kiwisoft.media.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.dataImport.DataSource;
import com.kiwisoft.media.ui.show.ShowLookup;
import com.kiwisoft.media.ui.show.EpisodeLookup;
import com.kiwisoft.media.ui.movie.MovieLookup;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.lookup.DateField;
import com.kiwisoft.utils.gui.lookup.LookupEvent;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupSelectionListener;
import com.kiwisoft.utils.gui.lookup.TimeField;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;

public class AirdateDetailsView extends DetailsView
{
	public static void create(Airdate airdate)
	{
		new DetailsFrame(new AirdateDetailsView(airdate)).show();
	}

	public static void create(Show show)
	{
		new DetailsFrame(new AirdateDetailsView(show)).show();
	}

	private Airdate airdate;
	private Show show;

	// Konfigurations Panel
	private DateField tfDate;
	private TimeField tfTime;
	private JComboBox cbxLanguage;
	private LookupField<Show> tfShow;
	private LookupField<Episode> tfEpisode;
	private LookupField<Movie> tfMovie;
	private JTextField tfEvent;
	private LookupField<Channel> tfChannel;
	private JTextField tfDataSource;

	private AirdateDetailsView(Airdate airdate)
	{
		this.airdate=airdate;
		setTitle("Sendetermin");
		createContentPanel();
		initializeData();
	}

	private AirdateDetailsView(Show show)
	{
		this.show=show;
		setTitle("Neuer Sendetermin");
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		tfDate=new DateField();
		tfTime=new TimeField();
		cbxLanguage=new JComboBox(LanguageManager.getInstance().getLanguages().toArray());
		cbxLanguage.updateUI();
		cbxLanguage.setRenderer(new LanguageComboBoxRenderer());
		tfShow=new LookupField<Show>(new ShowLookup());
		tfEpisode=new LookupField<Episode>(new DialogEpisodeLookup());
		tfMovie=new LookupField<Movie>(new MovieLookup());
		tfEvent=new JTextField();
		tfChannel=new LookupField<Channel>(new ChannelLookup());
		tfDataSource=new JTextField();
		tfDataSource.setEditable(false);

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 250));
		int row=0;
		add(new JLabel("Datum:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfDate, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(new JLabel("Zeit:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(tfTime, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Sender:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfChannel, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Sprache:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbxLanguage, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Ereignis:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfEvent, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
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
		add(new JLabel("Quelle:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfDataSource, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		tfShow.addSelectionListener(new ShowSelectionListener());
		tfMovie.addSelectionListener(new MovieSelectionListener());
	}


	private void initializeData()
	{
		if (airdate!=null)
		{
			Date date=airdate.getDate();
			if (date!=null)
			{
				tfDate.setDate(date);
				tfTime.setDate(date);
			}
			tfEvent.setText(airdate.getEvent());
			tfShow.setValue(airdate.getShow());
			tfEpisode.setValue(airdate.getEpisode());
			tfMovie.setValue(airdate.getMovie());
			tfChannel.setValue(airdate.getChannel());
			cbxLanguage.setSelectedItem(airdate.getLanguage());
			DataSource dataSource=airdate.getDataSource();
			if (dataSource!=null) tfDataSource.setText(dataSource.getName());
		}
		else
		{
			if (show!=null) tfShow.setValue(show);
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
		Date date=tfDate.getDate();
		if (date==null)
		{
			JOptionPane.showMessageDialog(this, "Datum fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfDate.requestFocus();
			return false;
		}
		Date time=tfTime.getDate();
		if (time==null)
		{
			JOptionPane.showMessageDialog(this, "Zeit fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfTime.requestFocus();
			return false;
		}
		Date fullDate=DateUtils.merge(date, time);
		Channel channel=tfChannel.getValue();
		if (channel==null)
		{
			JOptionPane.showMessageDialog(this, "Sender fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfChannel.requestFocus();
			return false;
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (airdate==null)
			{
				airdate=new Airdate();
				airdate.setDataSource(DataSource.INPUT);
			}
			airdate.setEvent(event);
			airdate.setShow(show);
			airdate.setEpisode(episode);
			airdate.setMovie(movie);
			airdate.setLanguage(language);
			airdate.setDate(fullDate);
			airdate.setChannel(channel);
			transaction.close();
			return true;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			try
			{
				if (transaction!=null) transaction.rollback();
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
			Episode episode=tfEpisode.getValue();
			if (episode!=null && episode.getShow()!=tfShow.getValue()) tfEpisode.setValue(null);
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
