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
import com.kiwisoft.media.dataImport.ImportEpisode;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.lookup.FileLookup;
import com.kiwisoft.swing.lookup.DateField;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;

public class EpisodeDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new EpisodeDetailsView(show, null)).show();
	}

	public static Episode createDialog(Window owner, Show show, ImportEpisode info)
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

	public static void create(Airdate airdate)
	{
		new DetailsFrame(new EpisodeDetailsView(airdate)).show();
	}

	public static void create(Episode episode)
	{
		new DetailsFrame(new EpisodeDetailsView(episode)).show();
	}

	private Show show;
	private Episode episode;
	private Airdate airdate;

	// Konfigurations Panel
	private JTextField userKeyField;
	private JTextField showField;
	private JTextField titleField;
	private JTextField germanTitleField;
	private JCheckBox seenField;
	private JCheckBox recordField;
	private JCheckBox goodField;
	private JTextField productionCodeField;
	private DateField firstAiredField;
	private JTextField javaScriptField;
	private DialogLookupField scriptFileField;
	private NamesTableModel namesModel;
	private JTextPane germanSummaryField;
	private JTextPane englishSummaryField;

	private EpisodeDetailsView(Show show, ImportEpisode info)
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

	private void initializeData(ImportEpisode importEpisode)
	{
		if (episode!=null)
		{
			titleField.setText(episode.getTitle());
			germanTitleField.setText(episode.getGermanTitle());
			if (episode.getShow()!=null) showField.setText(episode.getShow().getTitle());
			userKeyField.setText(episode.getUserKey());
			seenField.setSelected(episode.isSeen());
			recordField.setSelected(episode.isRecord());
			goodField.setSelected(episode.isGood());
			scriptFileField.setText(episode.getWebScriptFile());
			javaScriptField.setText(episode.getJavaScript());
			productionCodeField.setText(episode.getProductionCode());
			firstAiredField.setDate(episode.getAirdate());
			for (Name name : episode.getAltNames()) namesModel.addName(name.getName(), name.getLanguage());
			namesModel.sort();
			LanguageManager languageManager=LanguageManager.getInstance();
			germanSummaryField.setText(episode.getSummaryText(languageManager.getLanguageBySymbol("de")));
			englishSummaryField.setText(episode.getSummaryText(languageManager.getLanguageBySymbol("en")));
		}
		else if (show!=null)
		{
			showField.setText(show.getTitle());
			if (importEpisode!=null)
			{
				userKeyField.setText(importEpisode.getEpisodeKey());
				titleField.setText(importEpisode.getEpisodeTitle());
				germanTitleField.setText(importEpisode.getGermanEpisodeTitle());
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

	public boolean apply()
	{
		String number=userKeyField.getText();
		String title=titleField.getText();
		String germanTitle=germanTitleField.getText();
		boolean record=recordField.isSelected();
		boolean seen=seenField.isSelected();
		boolean good=goodField.isSelected();
		String script=scriptFileField.getText();
		if (StringUtils.isEmpty(script)) script=null;
		String javascript=javaScriptField.getText();
		if (StringUtils.isEmpty(javascript)) javascript=null;
		if (StringUtils.isEmpty(title) && StringUtils.isEmpty(germanTitle))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			titleField.requestFocus();
			return false;
		}
		Map<String, Language> names=namesModel.getNames();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (episode==null) episode=show.createEpisode();
			episode.setUserKey(number);
			episode.setTitle(title);
			episode.setGermanTitle(germanTitle);
			episode.setRecord(record);
			episode.setSeen(seen);
			episode.setGood(good);
			episode.setJavaScript(javascript);
			episode.setWebScriptFile(script);
			episode.setAirdate(firstAiredField.getDate());
			episode.setProductionCode(productionCodeField.getText());
			LanguageManager languageManager=LanguageManager.getInstance();
			episode.setSummaryText(languageManager.getLanguageBySymbol("de"), germanSummaryField.getText());
			episode.setSummaryText(languageManager.getLanguageBySymbol("en"), englishSummaryField.getText());
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
		goodField=new JCheckBox("Good");
		seenField=new JCheckBox("Seen");
		recordField=new JCheckBox("Record");
		scriptFileField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		javaScriptField=new JTextField();
		firstAiredField=new DateField();
		productionCodeField=new JTextField(10);
		namesModel=new NamesTableModel();
		SortableTable tblNames=new SortableTable(namesModel);
		tblNames.initializeColumns(new DefaultTableConfiguration(EpisodeDetailsView.class, "names"));
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
		panel.add(new JLabel("Script File:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(scriptFileField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("JavaScript:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(javaScriptField, new GridBagConstraints(1, row, 3, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Alternative Titles:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(namesPanel, new GridBagConstraints(1, row, 4, 3, 0.0, 0.0, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(seenField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(recordField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		panel.add(goodField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(5, 5, 0, 0), 0, 0));
		return panel;
	}

	protected JPanel createSummaryPanel()
	{
		germanSummaryField=new JTextPane();
		englishSummaryField=new JTextPane();
		JScrollPane germanSummaryPane=new JScrollPane(germanSummaryField);
		germanSummaryPane.setPreferredSize(new Dimension(400, 150));
		JScrollPane englishSummaryPane=new JScrollPane(englishSummaryField);
		englishSummaryPane.setPreferredSize(new Dimension(400, 150));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		int row=0;
		panel.add(new JLabel("English:"),
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		row++;
		panel.add(englishSummaryPane,
				new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, CENTER, BOTH, new Insets(5, 0, 0, 0), 0, 0));
		row++;
		panel.add(new JLabel("German:"),
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(11, 0, 0, 0), 0, 0));
		row++;
		panel.add(germanSummaryPane,
				new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, CENTER, BOTH, new Insets(5, 0, 0, 0), 0, 0));
		return panel;
	}

	public JComponent getDefaultFocusComponent()
	{
		return userKeyField;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=titleField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Episode: "+name);
		}
	}
}
