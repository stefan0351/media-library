package com.kiwisoft.media.show;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.*;
import com.kiwisoft.media.dataImport.ImportEpisode;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.DetailsDialog;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.lookup.DateField;
import com.kiwisoft.utils.gui.table.SortableTable;

public class EpisodeDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new EpisodeDetailsView(show, null)).show();
	}

	public static Episode createDialog(JFrame owner, Show show, ImportEpisode info)
	{
		EpisodeDetailsView view=new EpisodeDetailsView(show, info);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.episode;
		return null;
	}

	public static Episode createDialog(JFrame owner, Episode episode)
	{
		EpisodeDetailsView view=new EpisodeDetailsView(episode);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.episode;
		return null;
	}

	public static Episode createDialog(JDialog owner, Episode episode)
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
	private JTextField nameField;
	private JTextField originalNameField;
	private JCheckBox seenField;
	private JCheckBox recordField;
	private JCheckBox goodField;
	private JTextField productionCodeField;
	private DateField firstAiredField;
	private JTextField javaScriptField;
	private DialogLookupField scriptFileField;
	private NamesTableModel namesModel;

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
			nameField.setText(episode.getName());
			originalNameField.setText(episode.getOriginalName());
			if (episode.getShow()!=null) showField.setText(episode.getShow().getName());
			userKeyField.setText(episode.getUserKey());
			seenField.setSelected(episode.isSeen());
			recordField.setSelected(episode.isRecord());
			goodField.setSelected(episode.isGood());
			scriptFileField.setText(episode.getWebScriptFile());
			javaScriptField.setText(episode.getJavaScript());
			productionCodeField.setText(episode.getProductionCode());
			firstAiredField.setDate(episode.getAirdate());
			for (Iterator it=episode.getAltNames().iterator(); it.hasNext();)
			{
				Name name=(Name)it.next();
				namesModel.addName(name.getName(), name.getLanguage());
			}
			namesModel.sort();
		}
		else if (show!=null)
		{
			showField.setText(show.getName());
			if (importEpisode!=null)
			{
				userKeyField.setText(importEpisode.getEpisodeKey());
				nameField.setText(importEpisode.getEpisodeTitle());
				originalNameField.setText(importEpisode.getOriginalEpisodeTitle());
				firstAiredField.setDate(importEpisode.getFirstAirdate());
				productionCodeField.setText(importEpisode.getProductionCode());
			}
		}
		else if (airdate!=null)
		{
			showField.setText(airdate.getShow().getName());
			nameField.setText(airdate.getEvent());
			show=airdate.getShow();
		}
	}

	public boolean apply()
	{
		String number=userKeyField.getText();
		String name=nameField.getText();
		String originalName=originalNameField.getText();
		boolean record=recordField.isSelected();
		boolean seen=seenField.isSelected();
		boolean good=goodField.isSelected();
		String script=scriptFileField.getText();
		if (StringUtils.isEmpty(script)) script=null;
		String javascript=javaScriptField.getText();
		if (StringUtils.isEmpty(javascript)) javascript=null;
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(originalName))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			nameField.requestFocus();
			return false;
		}
		Map names=namesModel.getNames();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (episode==null) episode=show.createEpisode();
			episode.setUserKey(number);
			episode.setName(name);
			episode.setOriginalName(originalName);
			episode.setRecord(record);
			episode.setSeen(seen);
			episode.setGood(good);
			episode.setJavaScript(javascript);
			episode.setWebScriptFile(script);
			episode.setAirdate(firstAiredField.getDate());
			episode.setProductionCode(productionCodeField.getText());
			for (Iterator it=new HashSet(episode.getAltNames()).iterator(); it.hasNext();)
			{
				Name altName=(Name)it.next();
				if (names.containsKey(altName.getName()))
				{
					altName.setLanguage((Language)names.get(altName.getName()));
					names.remove(altName.getName());
				}
				else episode.dropAltName(altName);
			}
			for (Iterator it=names.keySet().iterator(); it.hasNext();)
			{
				String text=(String)it.next();
				Name altName=episode.createAltName();
				altName.setName(text);
				altName.setLanguage((Language)names.get(text));
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
		showField=new JTextField();
		showField.setEditable(false);
		nameField=new JTextField();
		originalNameField=new JTextField();
		userKeyField=new JTextField(5);
		userKeyField.setMinimumSize(new Dimension(100, userKeyField.getPreferredSize().height));
		goodField=new JCheckBox("Sehr Gut");
		seenField=new JCheckBox("Gesehen");
		recordField=new JCheckBox("Aufnehmen");
		scriptFileField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		javaScriptField=new JTextField();
		firstAiredField=new DateField();
		productionCodeField=new JTextField(10);
		namesModel=new NamesTableModel();
		SortableTable tblNames=new SortableTable(namesModel);
		tblNames.initializeColumns(new MediaTableConfiguration("table.episode.names"));
		JScrollPane namesPanel=new JScrollPane(tblNames);
		namesPanel.setPreferredSize(new Dimension(300, 100));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Serie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(showField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Number:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(userKeyField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Produktionsnummer:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(productionCodeField, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Erstausstrahlung:"), new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(firstAiredField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Originalname:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(originalNameField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Skriptdatei:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(scriptFileField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("JavaScript:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(javaScriptField, new GridBagConstraints(1, row, 3, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Alternative Titel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(namesPanel, new GridBagConstraints(1, row, 4, 3, 0.0, 0.0, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(seenField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(recordField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(goodField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(5, 5, 0, 0), 0, 0));

		nameField.getDocument().addDocumentListener(new FrameTitleUpdater());
	}

	public JComponent getDefaultFocusComponent()
	{
		return userKeyField;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("Episode: "+name);
		}
	}
}
