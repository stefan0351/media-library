package com.kiwisoft.media.show;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.pics.PictureLookup;
import com.kiwisoft.media.pics.PictureLookupHandler;
import com.kiwisoft.media.pics.PicturePreviewUpdater;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.utils.StringUtils;

public class SeasonDetailsView extends DetailsView
{
	private PicturePreviewUpdater previewUpdater;

	public static void create(Show show)
	{
		new DetailsFrame(new SeasonDetailsView(show)).show();
	}

	public static void create(Episode firstEpisode, Episode lastEpisode)
	{
		new DetailsFrame(new SeasonDetailsView(firstEpisode, lastEpisode)).show();
	}

	public static void create(Season season)
	{
		new DetailsFrame(new SeasonDetailsView(season)).show();
	}

	private Show show;
	private Season season;
	private Episode firstEpisode;
	private Episode lastEpisode;

	// Konfigurations Panel
	private JTextField showField;
	private JTextField numberField;
	private JTextField nameField;
	private JTextField altNameField;
	private JTextField startYearField;
	private JTextField endYearField;
	private LookupField<Episode> firstEpisodeField;
	private LookupField<Episode> lastEpisodeField;
	private LookupField<Picture> logoField;

	private SeasonDetailsView(Show show)
	{
		this.show=show;
		createContentPanel();
		initializeData();
	}

	private SeasonDetailsView(Season season)
	{
		this.season=season;
		this.show=season.getShow();
		createContentPanel();
		initializeData();
	}

	public SeasonDetailsView(Episode firstEpisode, Episode lastEpisode)
	{
		this.firstEpisode=firstEpisode;
		this.lastEpisode=lastEpisode;
		this.show=firstEpisode.getShow();
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		showField=new JTextField();
		showField.setEditable(false);
		nameField=new JTextField();
		nameField.setEditable(false);
		altNameField=new JTextField();
		numberField=new JTextField(5);
		numberField.setHorizontalAlignment(JTextField.TRAILING);
		startYearField=new JTextField(5);
		startYearField.setHorizontalAlignment(JTextField.TRAILING);
		endYearField=new JTextField(5);
		endYearField.setHorizontalAlignment(JTextField.TRAILING);
		firstEpisodeField=new LookupField<Episode>(new EpisodeLookup(show));
		lastEpisodeField=new LookupField<Episode>(new EpisodeLookup(show));
		logoField=new LookupField<Picture>(new PictureLookup(), new PictureLookupHandler()
		{
			@Override
			public String getDefaultName()
			{
				String name=altNameField.getText();
				if (StringUtils.isEmpty(name)) name=nameField.getText();
				return show.getTitle()+" - "+name+" - Logo";
			}
		});
		ImagePanel logoPreview=new ImagePanel(new Dimension(150, 150));
		logoPreview.setBorder(new EtchedBorder());

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(550, 230));
		int row=0;
		add(logoPreview, new GridBagConstraints(0, row, 1, 7, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 10), 0, 0));
		add(new JLabel("Show:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(showField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Number:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(numberField, new GridBagConstraints(2, row, 1, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(3, row, 2, 1, 0.7, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(altNameField, new GridBagConstraints(2, row, 3, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Start Year:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(startYearField, new GridBagConstraints(2, row, 1, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("End Year:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(endYearField, new GridBagConstraints(4, row, 1, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("First Episode:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(firstEpisodeField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Last Episode"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(lastEpisodeField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Logo"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(logoField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		numberField.getDocument().addDocumentListener(new NameUpdater());
		previewUpdater=new PicturePreviewUpdater(logoField, logoPreview);
	}

	private void initializeData()
	{
		if (season!=null)
		{
			nameField.setText(season.getSeasonName());
			altNameField.setText(season.getName());
			if (season.getShow()!=null) showField.setText(season.getShow().getTitle());
			numberField.setText(String.valueOf(season.getNumber()));
			firstEpisodeField.setValue(season.getFirstEpisode());
			lastEpisodeField.setValue(season.getLastEpisode());
			int startYear=season.getStartYear();
			if (startYear!=0) startYearField.setText(String.valueOf(startYear));
			int endYear=season.getEndYear();
			if (endYear!=0) endYearField.setText(String.valueOf(endYear));
			logoField.setValue(season.getLogo());
			previewUpdater.setDefaultPicture(show.getLogo());
		}
		else if (show!=null)
		{
			showField.setText(show.getTitle());
			firstEpisodeField.setValue(firstEpisode);
			lastEpisodeField.setValue(lastEpisode);
			if (firstEpisode!=null)
			{
				String userKey=firstEpisode.getUserKey();
				int pos=userKey.indexOf(".");
				if (pos>0) numberField.setText(userKey.substring(0, pos));
				Date firstAired=firstEpisode.getAirdate();
				if (firstAired!=null) startYearField.setText(new SimpleDateFormat("yyyy").format(firstAired));
			}
			if (lastEpisode!=null)
			{
				Date firstAired=lastEpisode.getAirdate();
				if (firstAired!=null) endYearField.setText(new SimpleDateFormat("yyyy").format(firstAired));
			}
			previewUpdater.setDefaultPicture(show.getLogo());
		}
	}

	public boolean apply()
	{
		try
		{
			String numberString=numberField.getText();
			if (StringUtils.isEmpty(numberString)) throw new InvalidDataException("Number is missing!", numberField);
			int number;
			try
			{
				number=Integer.parseInt(numberString);
			}
			catch (NumberFormatException e)
			{
				throw new InvalidDataException("Invalid number!", numberField);
			}
			if (number<0) throw new InvalidDataException("Number<0!", numberField);

			String startYearString=startYearField.getText();
			int startYear=0;
			try
			{
				if (!StringUtils.isEmpty(startYearString)) startYear=Integer.parseInt(startYearString);
			}
			catch (NumberFormatException e)
			{
				throw new InvalidDataException("Invalid year!", startYearField);
			}
			if (startYear<0) throw new InvalidDataException("Year<0!", startYearField);
			String endYearString=endYearField.getText();
			int endYear=0;
			try
			{
				if (!StringUtils.isEmpty(endYearString)) endYear=Integer.parseInt(endYearString);
			}
			catch (NumberFormatException e)
			{
				throw new InvalidDataException("Invalid year!", endYearField);
			}
			if (endYear<0) throw new InvalidDataException("Year<0!", endYearField);
			if (startYear==0 && endYear!=0) throw new InvalidDataException("Start year is missing !", startYearField);
			if (endYear!=0 && (endYear<startYear)) throw new InvalidDataException("End year<Start year", startYearField);

			Episode firstEpisode=firstEpisodeField.getValue();
			Episode lastEpisode=lastEpisodeField.getValue();
			if (firstEpisode==null && lastEpisode!=null) throw new InvalidDataException("First episode is missing!", firstEpisodeField);
			if (firstEpisode!=null && lastEpisode!=null && lastEpisode.getChainPosition()<firstEpisode.getChainPosition())
				throw new InvalidDataException("Last episode<First episode", firstEpisodeField);
			String name=altNameField.getText();
			Picture logo=logoField.getValue();

			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				if (season==null) season=show.createSeason();
				season.setNumber(number);
				season.setName(name);
				season.setStartYear(startYear);
				season.setEndYear(endYear);
				season.setFirstEpisode(firstEpisode);
				season.setLastEpisode(lastEpisode);
				season.setLogo(logo);
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
		catch (InvalidDataException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.getComponent().requestFocus();
			return false;
		}
	}

	public JComponent getDefaultFocusComponent()
	{
		return numberField;
	}

	private class NameUpdater extends DocumentAdapter
	{
		public NameUpdater()
		{
			changedUpdate(null);
		}

		public void changedUpdate(DocumentEvent e)
		{
			String name;
			try
			{
				name=Season.getName(Integer.parseInt(numberField.getText()));
			}
			catch (NumberFormatException e1)
			{
				name="";
			}
			nameField.setText(name);
			setTitle(name);
		}
	}
}
