package com.kiwisoft.media.show;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.*;
import com.kiwisoft.media.dataimport.EpisodeData;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.date.DateField;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.text.preformat.PreformatTextController;

public class EpisodeDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new EpisodeDetailsView(show, null)).show();
	}

	public static Episode createDialog(Window owner, Show show, EpisodeData info)
	{
		EpisodeDetailsView view=new EpisodeDetailsView(show, info);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.episode;
		return null;
	}

	public static Episode createDialog(Window owner, Episode episode)
	{
		EpisodeDetailsView view=new EpisodeDetailsView(episode);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.episode;
		return null;
	}

	public static void create(Window parent, Airdate airdate)
	{
		new DetailsFrame(parent, new EpisodeDetailsView(airdate)).show();
	}

	public static void create(Window parent, Episode episode)
	{
		if (parent instanceof JDialog) createDialog(parent, episode);
		else new DetailsFrame(parent, new EpisodeDetailsView(episode)).show();
	}

	private Show show;
	private Episode episode;
	private Airdate airdate;

	// Konfigurations Panel
	private JTextField userKeyField;
	private JTextField showField;
	private JTextField titleField;
	private JTextField germanTitleField;
	private JTextField productionCodeField;
	private DateField firstAiredField;
	private NamesTableModel namesModel;
	private PreformatTextController germanSummaryController;
	private PreformatTextController englishSummaryController;

	private EpisodeDetailsView(Show show, EpisodeData info)
	{
		this.show=show;
		createContentPanel();
		initializeData(info);
	}

	private EpisodeDetailsView(Episode episode)
	{
		this.episode=episode;
		show=episode.getShow();
		createContentPanel();
		initializeData(null);
	}

	private EpisodeDetailsView(Airdate airdate)
	{
		this.airdate=airdate;
		createContentPanel();
		initializeData(null);
	}

	private void initializeData(EpisodeData importEpisode)
	{
		if (episode!=null)
		{
			titleField.setText(episode.getTitle());
			germanTitleField.setText(episode.getGermanTitle());
			if (episode.getShow()!=null) showField.setText(episode.getShow().getTitle());
			userKeyField.setText(episode.getUserKey());
			productionCodeField.setText(episode.getProductionCode());
			firstAiredField.setDate(episode.getAirdate());
			for (Name name : episode.getAltNames()) namesModel.addName(name.getName(), name.getLanguage());
			namesModel.sort();
			LanguageManager languageManager=LanguageManager.getInstance();
			germanSummaryController.setText(episode.getSummaryText(languageManager.getLanguageBySymbol("de")));
			englishSummaryController.setText(episode.getSummaryText(languageManager.getLanguageBySymbol("en")));
		}
		else if (show!=null)
		{
			showField.setText(show.getTitle());
			if (importEpisode!=null)
			{
				userKeyField.setText(importEpisode.getKey());
				titleField.setText(importEpisode.getTitle());
				germanTitleField.setText(importEpisode.getGermanTitle());
				firstAiredField.setDate(importEpisode.getFirstAirdate());
				productionCodeField.setText(importEpisode.getProductionCode());
			}
		}
		else if (airdate!=null)
		{
			showField.setText(airdate.getShow().getTitle());
			titleField.setText(airdate.getEvent());
			show=airdate.getShow();
		}
	}

	@Override
	public boolean apply()
	{
		String number=userKeyField.getText();
		String title=titleField.getText();
		String germanTitle=germanTitleField.getText();
		if (StringUtils.isEmpty(title) && StringUtils.isEmpty(germanTitle))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			titleField.requestFocus();
			return false;
		}
		Map<String, Language> names=namesModel.getNameMap();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (episode==null) episode=show.createEpisode();
			episode.setUserKey(number);
			episode.setTitle(title);
			episode.setGermanTitle(germanTitle);
			episode.setAirdate(firstAiredField.getDate());
			episode.setProductionCode(productionCodeField.getText());
			LanguageManager languageManager=LanguageManager.getInstance();
			episode.setSummaryText(languageManager.getLanguageBySymbol("de"), germanSummaryController.getText());
			episode.setSummaryText(languageManager.getLanguageBySymbol("en"), englishSummaryController.getText());
			for (Name altName : new HashSet<Name>(episode.getAltNames()))
			{
				if (names.containsKey(altName.getName()))
				{
					altName.setLanguage(names.get(altName.getName()));
					names.remove(altName.getName());
				}
				else episode.dropAltName(altName);
			}
			for (String text : names.keySet())
			{
				Name altName=episode.createAltName();
				altName.setName(text);
				altName.setLanguage(names.get(text));
			}
			if (airdate!=null)
			{
				airdate.setEpisode(episode);
				airdate.setEvent(null);
			}
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

	protected void createContentPanel()
	{
		JTabbedPane tabs=new JTabbedPane();
		tabs.addTab("Details", createEpisodesPanel());
		tabs.addTab("Summary", createSummaryPanel());

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
		titleField.getDocument().addDocumentListener(new FrameTitleUpdater());
	}

	protected JPanel createEpisodesPanel()
	{
		showField=new JTextField();
		showField.setEditable(false);
		titleField=new JTextField();
		germanTitleField=new JTextField();
		userKeyField=new JTextField(5);
		userKeyField.setMinimumSize(new Dimension(100, userKeyField.getPreferredSize().height));
		firstAiredField=new DateField();
		productionCodeField=new JTextField(10);
		namesModel=new NamesTableModel(true);
		SortableTable tblNames=new SortableTable(namesModel);
		tblNames.configure(new DefaultTableConfiguration("EpisodeDetailsView.names", EpisodeDetailsView.class, "names"));
		JScrollPane namesPanel=new JScrollPane(tblNames);
		namesPanel.setPreferredSize(new Dimension(300, 100));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		int row=0;
		panel.add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(showField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Number:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(userKeyField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JLabel("Production Code:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(productionCodeField, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JLabel("First Aired:"), new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(firstAiredField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Title:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(titleField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("German Title:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(germanTitleField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Alternative Titles:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(namesPanel, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		return panel;
	}

	protected JPanel createSummaryPanel()
	{
		germanSummaryController=new PreformatTextController();
		englishSummaryController=new PreformatTextController();
		germanSummaryController.getComponent().setPreferredSize(new Dimension(400, 150));
		englishSummaryController.getComponent().setPreferredSize(new Dimension(400, 150));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		int row=0;
		panel.add(new JLabel("English:"),
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		row++;
		panel.add(englishSummaryController.getComponent(),
				new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, CENTER, BOTH, new Insets(5, 0, 0, 0), 0, 0));
		row++;
		panel.add(new JLabel("German:"),
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(11, 0, 0, 0), 0, 0));
		row++;
		panel.add(germanSummaryController.getComponent(),
				new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, CENTER, BOTH, new Insets(5, 0, 0, 0), 0, 0));
		return panel;
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return userKeyField;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=titleField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Episode: "+name);
		}
	}
}
