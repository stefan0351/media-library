package com.kiwisoft.media.show;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;
import java.io.IOException;
import java.io.File;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.*;
import com.kiwisoft.media.dataImport.XMLEpisodeInfo;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.EpisodeInfo;
import com.kiwisoft.media.NamesTableModel;
import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsDialog;

public class EpisodeDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new EpisodeDetailsView(show, null)).show();
	}

	public static Episode createDialog(JFrame owner, Show show, XMLEpisodeInfo info)
	{
		EpisodeDetailsView view=new EpisodeDetailsView(show, info);
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
	private JTextField tfUserKey;
	private JTextField tfShow;
	private JTextField tfName;
	private JTextField tfOriginalName;
	private JCheckBox cbSeen;
	private JCheckBox cbRecord;
	private JCheckBox cbGood;
	private JTextField tfJavaScript;
	private DialogLookupField tfScriptFile;
	private NamesTableModel tmNames;
	private DynamicTable tblInfos;
	private WebInfosTableModel tmInfos;
	private JComboBox cbxInfoTypes;

	private EpisodeDetailsView(Show show, XMLEpisodeInfo info)
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

	private void initializeData(XMLEpisodeInfo xmlInfo)
	{
		if (episode!=null)
		{
			tfName.setText(episode.getName());
			tfOriginalName.setText(episode.getOriginalName());
			if (episode.getShow()!=null) tfShow.setText(episode.getShow().getName());
			tfUserKey.setText(episode.getUserKey());
			cbSeen.setSelected(episode.isSeen());
			cbRecord.setSelected(episode.isRecord());
			cbGood.setSelected(episode.isGood());
			tfScriptFile.setText(episode.getWebScriptFile());
			tfJavaScript.setText(episode.getJavaScript());
			for (Iterator it=episode.getAltNames().iterator(); it.hasNext();)
			{
				Name name=(Name)it.next();
				tmNames.addName(name.getName(), name.getLanguage());
			}
			tmNames.sort();
			for (Iterator it=episode.getInfos().iterator(); it.hasNext();)
			{
				EpisodeInfo info=(EpisodeInfo)it.next();
				tmInfos.addInfo(info);
			}
			tmInfos.sort();
		}
		else if (show!=null)
		{
			tfShow.setText(show.getName());
			if (xmlInfo!=null)
			{
				tfName.setText(xmlInfo.getEpisode());
				tfOriginalName.setText(xmlInfo.getOriginalTitle());
			}
		}
		else if (airdate!=null)
		{
			tfShow.setText(airdate.getShow().getName());
			tfName.setText(airdate.getEvent());
			show=airdate.getShow();
		}
	}

	public boolean apply()
	{
		String number=tfUserKey.getText();
		String name=tfName.getText();
		String originalName=tfOriginalName.getText();
		boolean record=cbRecord.isSelected();
		boolean seen=cbSeen.isSelected();
		boolean good=cbGood.isSelected();
		String script=tfScriptFile.getText();
		if (StringUtils.isEmpty(script)) script=null;
		String javascript=tfJavaScript.getText();
		if (StringUtils.isEmpty(javascript)) javascript=null;
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(originalName))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		Map names=tmNames.getNames();
		List infos=new ArrayList();
		for (int i=0;i<tmInfos.getRowCount(); i++)
		{
			WebInfosTableModel.Row row=tmInfos.getRow(i);
			if (StringUtils.isEmpty(row.getName()))
			{
				JOptionPane.showMessageDialog(this, "Name für Info fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
				tblInfos.requestFocus();
				return false;
			}
			try
			{
				if (StringUtils.isEmpty(row.getPath()))
				{
					JOptionPane.showMessageDialog(this, "Pfad für Info fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
					tblInfos.requestFocus();
					return false;
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
				tblInfos.requestFocus();
				return false;
			}
			infos.add(row);
		}

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
			for (Iterator it=infos.iterator(); it.hasNext();)
			{
				WebInfosTableModel.Row row=(WebInfosTableModel.Row)it.next();
				EpisodeInfo info=(EpisodeInfo)row.getUserObject();
				if (info==null)
				{
					info=episode.createInfo();
					row.setUserObject(info);
				}
				info.setName(row.getName());
				info.setPath(row.getPath());
				info.setLanguage(row.getLanguage());
				if (row.isDefault()) episode.setDefaultInfo(info);
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
		tfShow=new JTextField();
		tfShow.setEditable(false);
		tfName=new JTextField();
		tfOriginalName=new JTextField();
		tfUserKey=new JTextField(5);
		tfUserKey.setMinimumSize(new Dimension(100, tfUserKey.getPreferredSize().height));
		cbGood=new JCheckBox("Sehr Gut");
		cbSeen=new JCheckBox("Gesehen");
		cbRecord=new JCheckBox("Aufnehmen");
		tfScriptFile=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		tfJavaScript=new JTextField();
		tmNames=new NamesTableModel();
		DynamicTable tblNames=new DynamicTable(tmNames);
		tblNames.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.episode.names"));
		tmInfos=new WebInfosTableModel(true);
		tblInfos=new DynamicTable(tmInfos);
		tblInfos.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.episode.infos"));
		Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
		Language english=LanguageManager.getInstance().getLanguageBySymbol("en");
		cbxInfoTypes=new JComboBox(new Object[]{
			"<Leer>",
			new EpisodeInfoType("Beschreibung (deutsch)", "info_de.xp", german),
			new EpisodeInfoType("Beschreibung (english)", "info.xp", english),
			new EpisodeInfoType("Bilder", "gallery.xp", english),
			new EpisodeInfoType("Transkript", "transcript.xp", english)
		});
		cbxInfoTypes.setEditable(false);

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(800, 400));
		int row=0;
		add(new JLabel("Serie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Number:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfUserKey, new GridBagConstraints(1, row, 1, 1, 0.3, 0.0,
											  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Originalname:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
																GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfOriginalName, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
												   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Skriptdatei:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfScriptFile, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("JavaScript:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfJavaScript, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Alternative Titel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
																	 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblNames), new GridBagConstraints(1, row, 1, 3, 1.0, 0.5,
															  GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(cbSeen, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(cbRecord, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
											 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(cbGood, new GridBagConstraints(2, row, 1, 1, 0.0, 0.2,
										   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Seiten:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblInfos), new GridBagConstraints(1, row, 1, 1, 1.0, 0.5,
															  GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(cbxInfoTypes, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
												 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		add(new JButton(new NewInfoAction()), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
																	 GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
	}

	public JComponent getDefaultFocusComponent()
	{
		return tfUserKey;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("Episode: "+name);
		}
	}

	private class NewInfoAction extends AbstractAction
	{
		public NewInfoAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			WebInfosTableModel.Row row=tmInfos.createRow();
			Object type=cbxInfoTypes.getSelectedItem();
			if (type instanceof EpisodeInfoType)
			{
				EpisodeInfoType infoType=(EpisodeInfoType)type;
				infoType.initRow(row);
			}
		}
	}

	private class EpisodeInfoType
	{
		private String name;
		private String fileName;
		private Language language;

		public EpisodeInfoType(String name, String fileName, Language language)
		{
			this.name=name;
			this.fileName=fileName;
			this.language=language;
		}

		public String toString()
		{
			return name;
		}

		public void initRow(WebInfosTableModel.Row row)
		{
			row.setName(name);
			String userKey=tfUserKey.getText();
			if (!StringUtils.isEmpty(userKey))
			{
				StringBuilder path=new StringBuilder("shows");
				path.append(File.separator).append(show.getUserKey());
				path.append(File.separator).append("episodes");
				path.append(File.separator).append(userKey.replace('.', File.separatorChar));
				path.append(File.separator).append(fileName);
				File file=new File(Configurator.getInstance().getString("path.root"), path.toString());
				row.setPath(file.getAbsolutePath());
			}
			row.setLanguage(language);
		}
	}


}
