package com.kiwisoft.media.show;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.InvalidDataException;

public class SeasonDetailsView extends DetailsView
{
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
	private JTextField tfShow;
	private JTextField tfNumber;
	private JTextField tfName;
	private JTextField tfSeasonName;
	private JTextField tfStartYear;
	private JTextField tfEndYear;
	private LookupField tfFirstEpisode;
	private LookupField tfLastEpisode;

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
		tfShow=new JTextField();
		tfShow.setEditable(false);
		tfName=new JTextField();
		tfName.setEditable(false);
		tfSeasonName=new JTextField();
		tfNumber=new JTextField(5);
		tfNumber.setHorizontalAlignment(JTextField.TRAILING);
		tfStartYear=new JTextField(5);
		tfStartYear.setHorizontalAlignment(JTextField.TRAILING);
		tfEndYear=new JTextField(5);
		tfEndYear.setHorizontalAlignment(JTextField.TRAILING);
		tfFirstEpisode=new LookupField(new EpisodeLookup(show));
		tfLastEpisode=new LookupField(new EpisodeLookup(show));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 200));
		int row=0;
		add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Number:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfNumber, new GridBagConstraints(1, row, 1, 1, 0.3, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(2, row, 2, 1, 0.7, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfSeasonName, new GridBagConstraints(1, row, 3, 1, 0.3, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Start Year:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfStartYear, new GridBagConstraints(1, row, 1, 1, 0.3, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("End Year:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfEndYear, new GridBagConstraints(3, row, 1, 1, 0.3, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("First Episode:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfFirstEpisode, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Last Episode"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfLastEpisode, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		tfNumber.getDocument().addDocumentListener(new NameUpdater());
	}

	private void initializeData()
	{
		if (season!=null)
		{
			tfName.setText(season.getSeasonName());
			tfSeasonName.setText(season.getName());
			if (season.getShow()!=null) tfShow.setText(season.getShow().getTitle());
			tfNumber.setText(String.valueOf(season.getNumber()));
			tfFirstEpisode.setValue(season.getFirstEpisode());
			tfLastEpisode.setValue(season.getLastEpisode());
			int startYear=season.getStartYear();
			if (startYear!=0) tfStartYear.setText(String.valueOf(startYear));
			int endYear=season.getEndYear();
			if (endYear!=0) tfEndYear.setText(String.valueOf(endYear));
		}
		else if (show!=null)
		{
			tfShow.setText(show.getTitle());
			tfFirstEpisode.setValue(firstEpisode);
			tfLastEpisode.setValue(lastEpisode);
			if (firstEpisode!=null)
			{
				String userKey=firstEpisode.getUserKey();
				int pos=userKey.indexOf(".");
				if (pos>0) tfNumber.setText(userKey.substring(0, pos));
				Date firstAired=firstEpisode.getAirdate();
				if (firstAired!=null) tfStartYear.setText(new SimpleDateFormat("yyyy").format(firstAired));
			}
			if (lastEpisode!=null)
			{
				Date firstAired=lastEpisode.getAirdate();
				if (firstAired!=null) tfEndYear.setText(new SimpleDateFormat("yyyy").format(firstAired));
			}
		}
	}

	public boolean apply()
	{
		try
		{
			String numberString=tfNumber.getText();
			if (StringUtils.isEmpty(numberString)) throw new InvalidDataException("Number is missing!", tfNumber);
			int number;
			try
			{
				number=Integer.parseInt(numberString);
			}
			catch (NumberFormatException e)
			{
				throw new InvalidDataException("Invalid number!", tfNumber);
			}
			if (number<0) throw new InvalidDataException("Number<0!", tfNumber);

			String startYearString=tfStartYear.getText();
			int startYear=0;
			try
			{
				if (!StringUtils.isEmpty(startYearString)) startYear=Integer.parseInt(startYearString);
			}
			catch (NumberFormatException e)
			{
				throw new InvalidDataException("Invalid year!", tfStartYear);
			}
			if (startYear<0) throw new InvalidDataException("Year<0!", tfStartYear);
			String endYearString=tfEndYear.getText();
			int endYear=0;
			try
			{
				if (!StringUtils.isEmpty(endYearString)) endYear=Integer.parseInt(endYearString);
			}
			catch (NumberFormatException e)
			{
				throw new InvalidDataException("Invalid year!", tfEndYear);
			}
			if (endYear<0) throw new InvalidDataException("Year<0!", tfEndYear);
			if (startYear==0 && endYear!=0) throw new InvalidDataException("Start year is missing !", tfStartYear);
			if (endYear!=0 && (endYear<startYear)) throw new InvalidDataException("End year<Start year", tfStartYear);

			Episode firstEpisode=(Episode)tfFirstEpisode.getValue();
			Episode lastEpisode=(Episode)tfLastEpisode.getValue();
			if (firstEpisode==null && lastEpisode!=null)
				throw new InvalidDataException("First episode is missing!", tfFirstEpisode);
			if (firstEpisode!=null && lastEpisode!=null && lastEpisode.getChainPosition()<firstEpisode.getChainPosition())
				throw new InvalidDataException("Last episode<First episode", tfFirstEpisode);
			String name=tfSeasonName.getText();

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
		return tfNumber;
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
				name=Season.getName(Integer.parseInt(tfNumber.getText()));
			}
			catch (NumberFormatException e1)
			{
				name="";
			}
			tfName.setText(name);
			setTitle(name);
		}
	}
}
