package com.kiwisoft.media.show;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.*;
import java.net.URLEncoder;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.*;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.lookup.DialogLookup;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.ObjectTableModel;
import com.kiwisoft.utils.gui.*;

public class ShowDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new ShowDetailsView(show)).show();
	}

	private Show show;

	// Konfigurations Panel
	private JTextField tfUserKey;
	private JTextField tfName;
	private JTextField tfOriginalName;
	private DialogLookupField tfDatesFile;
	private JComboBox cbxLanguage;
	private JTextField tfEpisodeLength;
	private DialogLookupField tfTVTVPattern;
	private JCheckBox cbWeb;
	private NamesTableModel tmNames;
	private DialogLookupField tfLogo;
	private SortableTable tblInfos;
	private WebInfosTableModel tmInfos;
	private ObjectTableModel tmGenres;

	private ShowDetailsView(Show show)
	{
		createContentPanel();
		setShow(show);
	}

	protected void createContentPanel()
	{
		tfUserKey=new JTextField();
		tfLogo=new DialogLookupField(new WebFileLookup(false));
		ImagePanel imgLogo=new ImagePanel(new Dimension(130, 70));
		imgLogo.setBorder(new EtchedBorder());
		tfName=new JTextField();
		tfOriginalName=new JTextField();
		tfEpisodeLength=new JTextField();
		tfEpisodeLength.setHorizontalAlignment(JTextField.TRAILING);
		tfDatesFile=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		tfTVTVPattern=new DialogLookupField(new TVTVPatternLookup());
		cbxLanguage=new JComboBox(LanguageManager.getInstance().getLanguages().toArray());
		cbxLanguage.updateUI();
		cbxLanguage.setRenderer(new LanguageComboBoxRenderer());
		tmNames=new NamesTableModel();
		SortableTable tblNames=new SortableTable(tmNames);
		tblNames.initializeColumns(new MediaTableConfiguration("table.show.names"));
		cbWeb=new JCheckBox();
		tmInfos=new WebInfosTableModel(false);
		tblInfos=new SortableTable(tmInfos);
		tblInfos.initializeColumns(new MediaTableConfiguration("table.show.infos"));
		tmGenres=new ObjectTableModel("genres", Genre.class, null);
		SortableTable tblGenres=new SortableTable(tmGenres);
		tblGenres.initializeColumns(new MediaTableConfiguration("table.show"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(700, 550));
		int row=0;
		add(new JLabel("Schlüssel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfUserKey, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Originalname:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfOriginalName, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Originalsprache:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbxLanguage, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(tblGenres), new GridBagConstraints(4, row, 1, 3, 0.1, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Folgenlänge:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfEpisodeLength, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Internet:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(cbWeb, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Suchmuster (TVTV.de):"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfTVTVPattern, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Termindatei:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfDatesFile, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Weitere Titel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblNames), new GridBagConstraints(1, row, 4, 1, 1.0, 0.5,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Logo:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfLogo, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(imgLogo, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Seiten:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblInfos), new GridBagConstraints(1, row, 3, 1, 1.0, 0.5,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(new JButton(new NewInfoAction()), new GridBagConstraints(4, row, 1, 1, 0.0, 0.5,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		FrameTitleUpdater frameTitleUpdater=new FrameTitleUpdater();
		tfName.getDocument().addDocumentListener(frameTitleUpdater);
		tfOriginalName.getDocument().addDocumentListener(frameTitleUpdater);
		new ImageUpdater(tfLogo.getTextField(), imgLogo);
	}

	private void setShow(Show show)
	{
		this.show=show;
		if (show!=null)
		{
			tfUserKey.setText(show.getUserKey());
			tfName.setText(show.getName());
			tfOriginalName.setText(show.getOriginalName());
			cbWeb.setSelected(show.isInternet());
			tfDatesFile.setText(show.getWebDatesFile());
			tfEpisodeLength.setText(String.valueOf(show.getDefaultEpisodeLength()));
			for (Genre genre : show.getGenres()) tmGenres.addObject(genre);
			tmGenres.addSortColumn(0, false);
			String pattern=show.getSearchPattern(SearchPattern.TVTV);
			if (pattern!=null) tfTVTVPattern.setText(pattern);
			Iterator it=show.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=(Name)it.next();
				tmNames.addName(name.getName(), name.getLanguage());
			}
			tmNames.sort();
			cbxLanguage.setSelectedItem(show.getLanguage());
			String logoMini=show.getLogoMini();
			if (!StringUtils.isEmpty(logoMini))
			{
				logoMini=new File(Configurator.getInstance().getString("path.root"), logoMini).getAbsolutePath();
				tfLogo.setText(logoMini);
			}
			for (Iterator itInfos=show.getInfos().iterator(); itInfos.hasNext();)
			{
				ShowInfo info=(ShowInfo)itInfos.next();
				tmInfos.addInfo(info);
			}
			tmInfos.sort();
		}
		else
		{
			tfEpisodeLength.setText("22");
		}
	}

	public boolean apply()
	{
		final String name=tfName.getText();
		final String originalName=tfOriginalName.getText();
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(originalName))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		final String userKey=tfUserKey.getText();
		if (StringUtils.isEmpty(userKey))
		{
			JOptionPane.showMessageDialog(this, "Schlüssel fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfUserKey.requestFocus();
			return false;
		}
		String episodeLength=tfEpisodeLength.getText();
		if (StringUtils.isEmpty(episodeLength))
		{
			JOptionPane.showMessageDialog(this, "Episodenlänge fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfEpisodeLength.requestFocus();
			return false;
		}
		final int length;
		try
		{
			length=Integer.parseInt(episodeLength);
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "Episodenlänge fehlerhaft!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfEpisodeLength.requestFocus();
			return false;
		}
		final Language language=(Language)cbxLanguage.getSelectedItem();
		if (language==null)
		{
			JOptionPane.showMessageDialog(this, "Sprache fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			cbxLanguage.requestFocus();
			return false;
		}
		final String tvtvPattern=tfTVTVPattern.getText();
		final Map<String, Language> names=tmNames.getNames();
		final Collection<Genre> genres=tmGenres.getObjects();
		String logoMini=tfLogo.getText();
		if (!StringUtils.isEmpty(logoMini))
		{
			try
			{
				logoMini=FileUtils.getRelativePath(Configurator.getInstance().getString("path.root"), logoMini);
				logoMini=StringUtils.replaceStrings(logoMini, "\\", "/");
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
				tfLogo.requestFocus();
				return false;
			}
		}
		else logoMini=null;
		final List<WebInfosTableModel.Row> infos=new ArrayList<WebInfosTableModel.Row>();
		for (int i=0;i<tmInfos.getRowCount(); i++)
		{
			WebInfosTableModel.Row row=(WebInfosTableModel.Row)tmInfos.getRow(i);
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
		final String logoMini1=logoMini;

		return DBSession.execute(new Transactional()
		{
			public void run() throws IOException
			{
				if (show==null) show=ShowManager.getInstance().createShow();
				show.setName(name);
				show.setOriginalName(originalName);
				show.setUserKey(userKey);
				show.setDefaultEpisodeLength(length);
				show.setInternet(cbWeb.isSelected());
				show.setWebDatesFile(tfDatesFile.getText());
				show.setSearchPattern(SearchPattern.TVTV, tvtvPattern);
				show.setLanguage(language);
				show.setLogoMini(logoMini1);
				show.setGenres(genres);
				for (Name altName : new HashSet<Name>(show.getAltNames()))
				{
					if (names.containsKey(altName.getName()))
					{
						altName.setLanguage(names.get(altName.getName()));
						names.remove(altName.getName());
					}
					else show.dropAltName(altName);
				}
				for (String text : names.keySet())
				{
					Name altName=show.createAltName();
					altName.setName(text);
					altName.setLanguage(names.get(text));
				}
				for (WebInfosTableModel.Row row : infos)
				{
					ShowInfo info=(ShowInfo)row.getUserObject();
					if (info==null)
					{
						info=show.createInfo();
						row.setUserObject(info);
					}
					info.setName(row.getName());
					info.setPath(row.getPath());
					info.setLanguage(row.getLanguage());
					if (row.isDefault()) show.setDefaultInfo(info);
				}
			}

			public void handleError(Throwable throwable)
			{
				JOptionPane.showMessageDialog(ShowDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name=tfOriginalName.getText();
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("Serie: "+name);
		}
	}

	private class TVTVPatternLookup implements DialogLookup
	{
		public void open(JTextField field)
		{
			try
			{
				field.setText(URLEncoder.encode(tfName.getText(), "UTF-8"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
            	JOptionPane.showMessageDialog(field, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		}

		public Icon getIcon()
		{
			return IconManager.getIcon("com/kiwisoft/utils/icons/lookup_create.gif");
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
			tmInfos.createRow();
		}
	}
}
