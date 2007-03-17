package com.kiwisoft.media.show;

import static java.awt.GridBagConstraints.BOTH;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.*;
import com.kiwisoft.media.dataImport.SearchPattern;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.lookup.DialogLookup;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.table.ObjectTableModel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;

public class ShowDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new ShowDetailsView(show)).show();
	}

	private Show show;

	// Konfigurations Panel
	private JTextField tfUserKey;
	private JTextField titleField;
	private JTextField germanTitleField;
	private DialogLookupField tfDatesFile;
	private JComboBox cbxLanguage;
	private JTextField tfEpisodeLength;
	private DialogLookupField tfTVTVPattern;
	private JCheckBox cbWeb;
	private NamesTableModel tmNames;
	private DialogLookupField tfLogo;
	private TableController<ShowInfo> infosController;
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
		ImagePanel imgLogo=new ImagePanel(new Dimension(150, 150));
		imgLogo.setBorder(new EtchedBorder());
		titleField=new JTextField();
		germanTitleField=new JTextField();
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

		WebInfosTableModel<ShowInfo> tmInfos=new WebInfosTableModel<ShowInfo>(false);
		infosController=new TableController<ShowInfo>(tmInfos, new MediaTableConfiguration("table.show.infos"))
		{
			@Override
			public List<ContextAction<ShowInfo>> getToolBarActions()
			{
				List<ContextAction<ShowInfo>> actions=new ArrayList<ContextAction<ShowInfo>>(1);
				actions.add(new NewInfoAction());
				return actions;
			}
		};
		tmGenres=new ObjectTableModel("genres", Genre.class, null);
		SortableTable tblGenres=new SortableTable(tmGenres);
		tblGenres.initializeColumns(new MediaTableConfiguration("table.show"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(800, 500));
		int row=0;
		add(imgLogo, new GridBagConstraints(0, row, 1, 8, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("Key:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(tfUserKey, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Title:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("German Title:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(germanTitleField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Language:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(cbxLanguage, new GridBagConstraints(2, row, 3, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(tblGenres), new GridBagConstraints(5, row, 1, 3, 0.5, 0.0, WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Episode Runtime:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfEpisodeLength, new GridBagConstraints(2, row, 1, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Internet:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(cbWeb, new GridBagConstraints(4, row, 1, 1, 0.3, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Search Parameter:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfTVTVPattern, new GridBagConstraints(2, row, 3, 1, 0.3, 0.0,WEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Schedule File:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfDatesFile, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0,WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Alternative Titel:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(new JScrollPane(tblNames), new GridBagConstraints(2, row, 4, 1, 1.0, 0.5,NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Logo:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfLogo, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0,NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Pages:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(infosController.createComponent(), new GridBagConstraints(2, row, 4, 1, 1.0, 0.5, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		FrameTitleUpdater frameTitleUpdater=new FrameTitleUpdater();
		titleField.getDocument().addDocumentListener(frameTitleUpdater);
		germanTitleField.getDocument().addDocumentListener(frameTitleUpdater);
		new ImageUpdater(tfLogo.getTextField(), imgLogo);
	}

	private void setShow(Show show)
	{
		this.show=show;
		if (show!=null)
		{
			tfUserKey.setText(show.getUserKey());
			titleField.setText(show.getTitle());
			germanTitleField.setText(show.getGermanTitle());
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
			WebInfosTableModel<ShowInfo> tmInfos=(WebInfosTableModel<ShowInfo>)infosController.getModel();
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
		final String title=titleField.getText();
		final String germanTitle=germanTitleField.getText();
		if (StringUtils.isEmpty(title) && StringUtils.isEmpty(germanTitle))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			titleField.requestFocus();
			return false;
		}
		final String userKey=tfUserKey.getText();
		if (StringUtils.isEmpty(userKey))
		{
			JOptionPane.showMessageDialog(this, "Key is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			tfUserKey.requestFocus();
			return false;
		}
		String episodeLength=tfEpisodeLength.getText();
		if (StringUtils.isEmpty(episodeLength))
		{
			JOptionPane.showMessageDialog(this, "Episode runtime is missing!", "Error", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(this, "Invalid episode runtime!", "Error", JOptionPane.ERROR_MESSAGE);
			tfEpisodeLength.requestFocus();
			return false;
		}
		final Language language=(Language)cbxLanguage.getSelectedItem();
		if (language==null)
		{
			JOptionPane.showMessageDialog(this, "Language is missing!", "Error", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				tfLogo.requestFocus();
				return false;
			}
		}
		else logoMini=null;
		final List<WebInfosTableModel.Row> infos=new ArrayList<WebInfosTableModel.Row>();
		SortableTableModel<ShowInfo> infosModel=infosController.getModel();
		for (int i=0; i<infosModel.getRowCount(); i++)
		{
			WebInfosTableModel.Row row=(WebInfosTableModel.Row)infosModel.getRow(i);
			if (StringUtils.isEmpty(row.getName()))
			{
				JOptionPane.showMessageDialog(this, "Name for page is missing!", "Error", JOptionPane.ERROR_MESSAGE);
				infosController.getTable().requestFocus();
				return false;
			}
			try
			{
				if (StringUtils.isEmpty(row.getPath()))
				{
					JOptionPane.showMessageDialog(this, "Path for page is missing!", "Error", JOptionPane.ERROR_MESSAGE);
					infosController.getTable().requestFocus();
					return false;
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				infosController.getTable().requestFocus();
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
				show.setTitle(title);
				show.setGermanTitle(germanTitle);
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
			String name=titleField.getText();
			if (StringUtils.isEmpty(name)) name=germanTitleField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Show: "+name);
		}
	}

	private class TVTVPatternLookup implements DialogLookup
	{
		public void open(JTextField field)
		{
			try
			{
				field.setText(URLEncoder.encode(germanTitleField.getText(), "UTF-8"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(field, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		}

		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}

	private class NewInfoAction extends ContextAction<ShowInfo>
	{
		public NewInfoAction()
		{
			super("New");
		}

		public void actionPerformed(ActionEvent e)
		{
			((WebInfosTableModel<ShowInfo>)infosController.getModel()).createRow();
		}
	}
}
