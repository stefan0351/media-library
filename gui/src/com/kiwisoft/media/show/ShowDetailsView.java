package com.kiwisoft.media.show;

import static java.awt.GridBagConstraints.BOTH;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.*;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.PicturePreviewUpdater;
import com.kiwisoft.media.files.MediaFileLookup;
import com.kiwisoft.media.files.ImageLookupHandler;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.lookup.*;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.table.*;
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
	private JCheckBox webShowField;
	private NamesTableModel tmNames;
	private LookupField<MediaFile> logoField;
	private TableController<ShowInfo> infosController;
	private ObjectTableModel<Genre> genresModel;

	private ShowDetailsView(Show show)
	{
		createContentPanel();
		setShow(show);
	}

	protected void createContentPanel()
	{
		indexByField=new DialogLookupField(new IndexByLookup());
		keyField=new JTextField();
		logoField=new LookupField<MediaFile>(new MediaFileLookup(MediaFile.IMAGE), new ImageLookupHandler()
		{
			@Override
			public String getDefaultName()
			{
				return titleField.getText()+" - Logo";
			}
		});
		ImagePanel logoPreview=new ImagePanel(new Dimension(150, 150));
		logoPreview.setBorder(new EtchedBorder());
		titleField=new JTextField();
		germanTitleField=new JTextField();
		episodeLengthField=new JTextField();
		episodeLengthField.setHorizontalAlignment(JTextField.TRAILING);
		scheduleFileField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		languageField=new LookupField<Language>(new LanguageLookup());
		tmNames=new NamesTableModel(true);
		SortableTable tblNames=new SortableTable(tmNames);
		tblNames.initializeColumns(new DefaultTableConfiguration(ShowDetailsView.class, "names"));
		webShowField=new JCheckBox();

		WebInfosTableModel<ShowInfo> tmInfos=new WebInfosTableModel<ShowInfo>(false);
		infosController=new TableController<ShowInfo>(tmInfos, new DefaultTableConfiguration(ShowDetailsView.class, "infos"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(2);
				actions.add(new NewInfoAction());
				actions.add(new DeleteInfoAction());
				return actions;
			}
		};
		genresModel=new ObjectTableModel<Genre>("name", Genre.class, null);
		SortableTable tblGenres=new SortableTable(genresModel);
		tblGenres.initializeColumns(new DefaultTableConfiguration(ShowDetailsView.class, "genres"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(800, 500));
		int row=0;
		add(logoPreview, new GridBagConstraints(0, row, 1, 8, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
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
		add(new JScrollPane(tblGenres), new GridBagConstraints(5, row, 1, 3, 0.5, 0.0, WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Language:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(languageField, new GridBagConstraints(2, row, 3, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Episode Runtime:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(episodeLengthField, new GridBagConstraints(2, row, 1, 1, 0.3, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Internet:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(webShowField, new GridBagConstraints(4, row, 1, 1, 0.3, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Schedule File:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(scheduleFileField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0,WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Alternative Titel:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(new JScrollPane(tblNames), new GridBagConstraints(2, row, 4, 1, 1.0, 0.5,NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Logo:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(logoField, new GridBagConstraints(2, row, 4, 1, 1.0, 0.0,NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Pages:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(infosController.createComponent(), new GridBagConstraints(2, row, 4, 1, 1.0, 0.5, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		FrameTitleUpdater frameTitleUpdater=new FrameTitleUpdater();
		titleField.getDocument().addDocumentListener(frameTitleUpdater);
		germanTitleField.getDocument().addDocumentListener(frameTitleUpdater);
		new PicturePreviewUpdater(logoField, logoPreview);
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
			Iterator it=show.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=(Name)it.next();
				tmNames.addName(name.getName(), name.getLanguage());
			}
			tmNames.sort();
			languageField.setValue(show.getLanguage());
			logoField.setValue(show.getLogo());
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
		final Map<String, Language> names=tmNames.getNameMap();
		final Collection<Genre> genres=genresModel.getObjects();
		final MediaFile logo=logoField.getValue();
		final List<WebInfosTableModel<ShowInfo>.Row> infoRows=new ArrayList<WebInfosTableModel<ShowInfo>.Row>();
		SortableTableModel<ShowInfo> infosModel=infosController.getModel();
		for (int i=0; i<infosModel.getRowCount(); i++)
		{
			WebInfosTableModel<ShowInfo>.Row row=(WebInfosTableModel.Row)infosModel.getRow(i);
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
				show.setLanguage(language);
				show.setLogo(logo);
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
				for (WebInfosTableModel<ShowInfo>.Row row : infoRows)
				{
					ShowInfo info=row.getUserObject();
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

	private class NewInfoAction extends ContextAction
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

	private class DeleteInfoAction extends MultiContextAction
	{
		public DeleteInfoAction()
		{
			super(ShowInfo.class,  "Delete", Icons.getIcon("delete"));
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
			String title=titleField.getText();
			if (StringUtils.isEmpty(title)) title=germanTitleField.getText();
			field.setText(IndexByUtils.createIndexBy(title));
		}

		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}
}
