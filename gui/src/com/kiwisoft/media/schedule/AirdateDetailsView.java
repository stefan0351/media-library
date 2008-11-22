package com.kiwisoft.media.schedule;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;

import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.*;
import com.kiwisoft.media.dataimport.DataSource;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.media.show.EpisodeLookup;
import com.kiwisoft.media.movie.MovieLookup;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Time;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.lookup.DateField;
import com.kiwisoft.swing.lookup.LookupEvent;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupSelectionListener;
import com.kiwisoft.swing.lookup.TimeField;
import com.kiwisoft.swing.ActionField;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;

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
	private DateField dateField;
	private TimeField timeField;
	private LookupField<Language> languageField;
	private LookupField<Show> showField;
	private LookupField<Episode> episodeField;
	private LookupField<Movie> movieField;
	private JTextField eventField;
	private LookupField<Channel> channelField;
	private JTextField dataSourceField;
	private ActionField linkField;

	private AirdateDetailsView(Airdate airdate)
	{
		this.airdate=airdate;
		setTitle("Airdate");
		createContentPanel();
		initializeData();
	}

	private AirdateDetailsView(Show show)
	{
		this.show=show;
		setTitle("New Airdate");
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		dateField=new DateField();
		timeField=new TimeField();
		languageField=new LookupField<Language>(new LanguageLookup());
		showField=new LookupField<Show>(new ShowLookup());
		episodeField=new LookupField<Episode>(new DialogEpisodeLookup());
		movieField=new LookupField<Movie>(new MovieLookup());
		eventField=new JTextField();
		channelField=new LookupField<Channel>(new ChannelLookup());
		dataSourceField=new JTextField();
		dataSourceField.setEditable(false);
		linkField=new ActionField(new OpenLinkAction());
		linkField.setEditable(false);

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 275));
		int row=0;
		add(new JLabel("Date:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(dateField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(new JLabel("Time:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(timeField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Channel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(channelField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Language:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(languageField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Event:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(eventField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
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
		add(new JLabel("Source:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(dataSourceField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Link:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(linkField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		getListenerList().installSelectionListener(showField, new ShowSelectionListener());
		getListenerList().installSelectionListener(movieField, new MovieSelectionListener());
	}

	private void initializeData()
	{
		if (airdate!=null)
		{
			Date date=airdate.getDate();
			if (date!=null)
			{
				Time time=DateUtils.getTime(date, true);
				dateField.setDate(date);
				timeField.setTime(time);
			}
			eventField.setText(airdate.getEvent());
			showField.setValue(airdate.getShow());
			episodeField.setValue(airdate.getEpisode());
			movieField.setValue(airdate.getMovie());
			channelField.setValue(airdate.getChannel());
			languageField.setValue(airdate.getLanguage());
			DataSource dataSource=airdate.getDataSource();
			if (dataSource!=null) dataSourceField.setText(dataSource.getName());
			linkField.setText(airdate.getDetailsLink());
		}
		else
		{
			if (show!=null) showField.setValue(show);
			languageField.setValue(LanguageManager.getInstance().getLanguageBySymbol("de"));
		}
	}

	public boolean apply()
	{
		String event=eventField.getText();
		if (StringUtils.isEmpty(event)) event=null;
		Show show=showField.getValue();
		Episode episode=episodeField.getValue();
		Movie movie=movieField.getValue();
		Language language=languageField.getValue();
		Date date=dateField.getDate();
		if (date==null)
		{
			JOptionPane.showMessageDialog(this, "Date is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			dateField.requestFocus();
			return false;
		}
		Time time=timeField.getTime();
		if (time==null)
		{
			JOptionPane.showMessageDialog(this, "Time is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			timeField.requestFocus();
			return false;
		}
		Date fullDate=DateUtils.merge(date, time);
		Channel channel=channelField.getValue();
		if (channel==null)
		{
			JOptionPane.showMessageDialog(this, "Channel is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			channelField.requestFocus();
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
			Episode episode=episodeField.getValue();
			if (episode!=null && episode.getShow()!=showField.getValue()) episodeField.setValue(null);
		}
	}

	private class MovieSelectionListener implements LookupSelectionListener
	{
		public void selectionChanged(LookupEvent event)
		{
			Movie movie=movieField.getValue();
			if (movie!=null) showField.setValue(movie.getShow());
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

	private class OpenLinkAction extends AbstractAction
	{
		public OpenLinkAction()
		{
			super(null, Icons.getIcon("link.open"));
		}

		public void actionPerformed(ActionEvent e)
		{
			String link=linkField.getText();
			if (!StringUtils.isEmpty(link))
			{
				try
				{
					WebUtils.openURL(new URL(link));
				}
				catch (MalformedURLException e1)
				{
					GuiUtils.handleThrowable(linkField, e1);
				}
			}
		}
	}
}
