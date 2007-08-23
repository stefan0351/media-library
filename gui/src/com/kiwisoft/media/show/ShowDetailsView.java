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
import com.kiwisoft.utils.gui.table.TableController;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.lookup.DialogLookup;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.table.*;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;

public class ShowDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new ShowDetailsView(show)).show();
	}

	private Show show;

	// Konfigurations Panel
	private JTextField keyField;
	private JTextField titleField;
	private JTextField germanTitleField;
	private DialogLookupField scheduleFileField;
	private DialogLookupField indexByField;
	private LookupField<Language> languageField;
	private JTextField episodeLengthField;
	private DialogLookupField patternField;
	private JCheckBox webShowField;
	private NamesTableModel tmNames;
	private DialogLookupField tfLogo;
	private TableController<ShowInfo> infosController;
	private ObjectTableModel genresModel;

	private ShowDetailsView(Show show)
	{
		createContentPanel();
		setShow(show);
	}

	protected void createContentPanel()
	{
		indexByField=new DialogLookupField(new IndexByLookup());
		keyField=new JTextField();
		tfLogo=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		ImagePanel imgLogo=new ImagePanel(new Dimension(150, 150));
		imgLogo.setBorder(new EtchedBorder());
		titleField=new JTextField();
		germanTitleField=new JTextField();
		episodeLengthField=new JTextField();
		episodeLengthField.setHorizontalAlignment(JTextField.TRAILING);
		scheduleFileField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		patternField=new DialogLookupField(new TVTVPatternLookup());
		languageField=new LookupField<Language>(new LanguageLookup());
		tmNames=new NamesTableModel();
		SortableTable tblNames=new SortableTable(tmNames);
		tblNames.initializeColumns(new DefaultTableConfiguration(ShowDetailsView.class, "names"));
		webShowField=new JCheckBox();

		WebInfosTableModel<ShowInfo> tmInfos=new WebInfosTableModel<ShowInfo>(false);
		infosController=new TableController<ShowInfo>(tmInfos, new DefaultTableConfiguration(ShowDetailsView.class, "infos"))
		{
			@Override
			public List<ContextAction<? super ShowInfo>> getToolBarActions()
			{
				List<ContextAction<? super ShowInfo>> actions=new ArrayList<ContextAction<? super ShowInfo>>(2);
				actions.add(new NewInfoAction());
				actions.add(new DeleteInfoAction());
				return actions;
			}
		};
		genresModel=new ObjectTableModel("name", Genre.class, null);
		SortableTable tblGenres=new SortableTable(genresModel);
		tblGenres.initializeColumns(new DefaultTableConfiguration(ShowDetailsView.class, "genres"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(800, 500));
		int row=0;
		add(imgLogo, new GridBagConstraints(0, row, 1, 8, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("Key:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(keyField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Title:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("German Title:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(germanTitleField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Index By:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(indexByField, new GridBagConstraints(2, row, 3, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(tblGenres), new GridBagConstraints(5, row, 1, 4, 0.5, 0.0, WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Language:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(languageField, new GridBagConstraints(2, row, 3, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Episode Runtime:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(episodeLengthField, new GridBagConstraints(2, row, 1, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Internet:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(webShowField, new GridBagConstraints(4, row, 1, 1, 0.3, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Search Parameter:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(patternField, new GridBagConstraints(2, row, 3, 1, 0.3, 0.0,WEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Schedule File:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(scheduleFileField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0,WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
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
		infosController.installListeners();
	}


	@Override
	public void dispose()
	{
		infosController.removeListeners();
		infosController.dispose();
		super.dispose();
	}

	private void setShow(Show show)
	{
		this.show=show;
		if (show!=null)
		{
			keyField.setText(show.getUserKey());
			titleField.setText(show.getTitle());
			germanTitleField.setText(show.getGermanTitle());
			indexByField.setText(show.getIndexBy());
			webShowField.setSelected(show.isInternet());
			scheduleFileField.setText(show.getWebDatesFile());
			episodeLengthField.setText(String.valueOf(show.getDefaultEpisodeLength()));
			for (Genre genre : show.getGenres()) genresModel.addObject(genre);
			genresModel.addSortColumn(0, false);
			String pattern=show.getSearchPattern(SearchPattern.TVTV);
			if (pattern!=null) patternField.setText(pattern);
			Iterator it=show.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=(Name)it.next();
				tmNames.addName(name.getName(), name.getLanguage());
			}
			tmNames.sort();
			languageField.setValue(show.getLanguage());
			String logoMini=show.getLogoMini();
			if (!StringUtils.isEmpty(logoMini))
			{
				logoMini=new File(MediaConfiguration.getRootPath(), logoMini).getAbsolutePath();
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
			episodeLengthField.setText("22");
		}
	}

	public boolean apply()
	{
		final String title=titleField.getText();
		final String germanTitle=germanTitleField.getText();
		if (StringUtils.isEmpty(title))
		{
			JOptionPane.showMessageDialog(this, "Title is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			titleField.requestFocus();
			return false;
		}
		final String indexBy=indexByField.getText();
		if (StringUtils.isEmpty(indexBy))
		{
			JOptionPane.showMessageDialog(this, "Index By is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			indexByField.requestFocus();
			return false;
		}
		final String userKey=keyField.getText();
		if (StringUtils.isEmpty(userKey))
		{
			JOptionPane.showMessageDialog(this, "Key is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			keyField.requestFocus();
			return false;
		}
		String episodeLength=episodeLengthField.getText();
		if (StringUtils.isEmpty(episodeLength))
		{
			JOptionPane.showMessageDialog(this, "Episode runtime is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			episodeLengthField.requestFocus();
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
			episodeLengthField.requestFocus();
			return false;
		}
		final Language language=languageField.getValue();
		if (language==null)
		{
			JOptionPane.showMessageDialog(this, "Language is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			languageField.requestFocus();
			return false;
		}
		final String tvtvPattern=patternField.getText();
		final Map<String, Language> names=tmNames.getNames();
		final Collection<Genre> genres=genresModel.getObjects();
		String logoMini=tfLogo.getText();
		if (!StringUtils.isEmpty(logoMini))
		{
			logoMini=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), logoMini);
			logoMini=StringUtils.replaceStrings(logoMini, "\\", "/");
		}
		else logoMini=null;
		final List<WebInfosTableModel.Row> infoRows=new ArrayList<WebInfosTableModel.Row>();
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
			infoRows.add(row);
		}
		final String logoMini1=logoMini;

		return DBSession.execute(new Transactional()
		{
			public void run() throws IOException
			{
				if (show==null) show=ShowManager.getInstance().createShow();
				show.setTitle(title);
				show.setIndexBy(indexBy);
				show.setGermanTitle(germanTitle);
				show.setUserKey(userKey);
				show.setDefaultEpisodeLength(length);
				show.setInternet(webShowField.isSelected());
				show.setWebDatesFile(scheduleFileField.getText());
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
				Set<ShowInfo> removedInfos=new HashSet<ShowInfo>(show.getInfos());
				for (WebInfosTableModel.Row row : infoRows)
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
					removedInfos.remove(info);
					if (row.isDefault()) show.setDefaultInfo(info);
				}
				for (ShowInfo info : removedInfos)
				{
					show.dropInfo(info);
				}
			}

			public void handleError(Throwable throwable, boolean rollback)
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
				JOptionPane.showMessageDialog(field, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
			super("New", Icons.getIcon("add"));
		}

		public void actionPerformed(ActionEvent e)
		{
			((WebInfosTableModel<ShowInfo>)infosController.getModel()).createRow();
		}
	}

	private class DeleteInfoAction extends MultiContextAction<ShowInfo>
	{
		public DeleteInfoAction()
		{
			super("Delete", Icons.getIcon("delete"));
		}

		public void actionPerformed(ActionEvent e)
		{
			for (Object object : getObjects()) infosController.getModel().removeObject(object);
		}
	}

	private class IndexByLookup implements DialogLookup
	{
		public void open(JTextField field)
		{
			field.setText(IndexByUtils.createIndexBy(titleField.getText()));
		}

		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}
}
