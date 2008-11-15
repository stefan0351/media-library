package com.kiwisoft.media.movie;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.*;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.pics.PictureLookup;
import com.kiwisoft.media.pics.PictureLookupHandler;
import com.kiwisoft.media.pics.PicturePreviewUpdater;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.*;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.lookup.DialogLookup;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.lookup.FileLookup;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.ObjectTableModel;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.utils.StringUtils;

public class MovieDetailsView extends DetailsView
{
	public static final String PATH_ROOT="path.root";

	public static void create(Show show)
	{
		new DetailsFrame(new MovieDetailsView(show)).show();
	}

	public static void create(Movie movie)
	{
		new DetailsFrame(new MovieDetailsView(movie)).show();
	}

	public static Movie createDialog(Window owner, String text)
	{
		MovieDetailsView view=new MovieDetailsView(text);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.movie;
		return null;
	}

	private Show show;
	private Movie movie;

	// Konfigurations Panel
	private JTextField showField;
	private JTextField titleField;
	private DialogLookupField indexByField;
	private JTextField germanTitleField;
	private JCheckBox recordField;
	private JTextField javaScriptField;
	private DialogLookupField transcriptField;
	private NamesTableModel namesModel;
	private JFormattedTextField yearField;
	private JFormattedTextField runtimeField;
	private ObjectTableModel<Genre> genresModel;
	private ObjectTableModel<Language> languagesModel;
	private ObjectTableModel<Country> countriesModel;
	private LookupField<Picture> posterField;
	private JTextPane germanSummaryField;
	private JTextPane englishSummaryField;

	private MovieDetailsView(Show show)
	{
		this.show=show;
		createContentPanel();
		initializeData();
	}

	private MovieDetailsView(Movie movie)
	{
		this.movie=movie;
		createContentPanel();
		initializeData();
	}

	private MovieDetailsView(String text)
	{
		createContentPanel();
		initializeData();
		titleField.setText(text);
	}

	private void initializeData()
	{
		if (movie!=null)
		{
			titleField.setText(movie.getTitle());
			indexByField.setText(movie.getIndexBy());
			germanTitleField.setText(movie.getGermanTitle());
			if (movie.getShow()!=null) showField.setText(movie.getShow().getTitle());
			recordField.setSelected(movie.isRecord());
			transcriptField.setText(movie.getWebScriptFile());
			javaScriptField.setText(movie.getJavaScript());
			yearField.setValue(movie.getYear());
			runtimeField.setValue(movie.getRuntime());
			Iterator<Name> it=movie.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=it.next();
				namesModel.addName(name.getName(), name.getLanguage());
			}
			namesModel.sort();
			genresModel.setObjects(movie.getGenres());
			languagesModel.setObjects(movie.getLanguages());
			countriesModel.setObjects(movie.getCountries());
			posterField.setValue(movie.getPoster());
			germanSummaryField.setText(movie.getSummaryText(LanguageManager.GERMAN));
			englishSummaryField.setText(movie.getSummaryText(LanguageManager.ENGLISH));
		}
		else if (show!=null)
		{
			showField.setText(show.getTitle());
		}
	}

	public boolean apply() throws InvalidDataException
	{
		final String name=titleField.getText();
		if (StringUtils.isEmpty(name)) throw new InvalidDataException("Title is missing!", titleField);
		final String indexBy=indexByField.getText();
		if (StringUtils.isEmpty(indexBy)) throw new InvalidDataException("Index by is missing!", indexByField);
		final String germanName=germanTitleField.getText();
		final boolean record=recordField.isSelected();
		final String script=StringUtils.empty2null(transcriptField.getText());
		final String javascript=StringUtils.empty2null(javaScriptField.getText());
		final Map<String, Language> names=namesModel.getNameMap();
		final Collection<Language> languages=languagesModel.getObjects();
		final Collection<Country> countries=countriesModel.getObjects();
		final Collection<Genre> genres=genresModel.getObjects();
		final Picture poster=posterField.getValue();

		return DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				if (movie==null)
				{
					if (show!=null) movie=show.createMovie();
					else movie=MovieManager.getInstance().createMovie(null);
				}
				movie.setTitle(name);
				movie.setIndexBy(indexBy);
				movie.setGermanTitle(germanName);
				movie.setRecord(record);
				movie.setJavaScript(javascript);
				movie.setPoster(poster);
				movie.setWebScriptFile(script);
				movie.setYear((Integer)yearField.getValue());
				movie.setRuntime((Integer)runtimeField.getValue());
				movie.setGenres(genres);
				movie.setLanguages(languages);
				movie.setCountries(countries);
				Iterator<Name> it=new HashSet<Name>(movie.getAltNames()).iterator();
				while (it.hasNext())
				{
					Name altName=it.next();
					if (names.containsKey(altName.getName()))
					{
						altName.setLanguage(names.get(altName.getName()));
						names.remove(altName.getName());
					}
					else movie.dropAltName(altName);
				}
				Iterator<String> itNames=names.keySet().iterator();
				while (itNames.hasNext())
				{
					String text=itNames.next();
					Name altName=movie.createAltName();
					altName.setName(text);
					altName.setLanguage(names.get(text));
				}
				movie.setSummaryText(LanguageManager.GERMAN, germanSummaryField.getText());
				movie.setSummaryText(LanguageManager.ENGLISH, englishSummaryField.getText());
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(MovieDetailsView.this, throwable);
			}
		});
	}

	protected void createContentPanel()
	{
		JTabbedPane tabs=new JTabbedPane();
		tabs.addTab("Details", createDetailsPanel());
		tabs.addTab("Summary", createSummaryPanel());

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
		titleField.getDocument().addDocumentListener(new FrameTitleUpdater());
	}

	private JPanel createDetailsPanel()
	{
		posterField=new LookupField<Picture>(new PictureLookup(), new PictureLookupHandler()
		{
			@Override
			public String getDefaultName()
			{
				return titleField.getText()+" - Poster";
			}
		});
		ImagePanel posterPreview=new ImagePanel(new Dimension(150, 200));
		posterPreview.setBorder(new EtchedBorder());
		showField=new JTextField();
		showField.setEditable(false);
		titleField=new JTextField();
		indexByField=new DialogLookupField(new IndexByLookup());
		germanTitleField=new JTextField();
		recordField=new JCheckBox("Record");
		transcriptField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		javaScriptField=new JTextField(20);
		namesModel=new NamesTableModel(true);
		yearField=ComponentUtils.createNumberField(Integer.class, 5, 1900, 2100);
		runtimeField=ComponentUtils.createNumberField(Integer.class, 5, 0, 500);
		SortableTable tblNames=new SortableTable(namesModel);
		tblNames.setPreferredScrollableViewportSize(new Dimension(300, 100));
		tblNames.initializeColumns(new DefaultTableConfiguration(MovieDetailsView.class, "names"));
		genresModel=new ObjectTableModel<Genre>("name", Genre.class, null);
		SortableTable genresTable=new SortableTable(genresModel);
		genresTable.initializeColumns(new DefaultTableConfiguration(MovieDetailsView.class, "genres"));
		languagesModel=new ObjectTableModel<Language>("name", Language.class, null);
		SortableTable languagesTable=new SortableTable(languagesModel);
		languagesTable.initializeColumns(new DefaultTableConfiguration(MovieDetailsView.class, "languages"));
		countriesModel=new ObjectTableModel<Country>("name", Country.class, null);
		SortableTable countriesTable=new SortableTable(countriesModel);
		countriesTable.initializeColumns(new DefaultTableConfiguration(MovieDetailsView.class, "countries"));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.setPreferredSize(new Dimension(800, 340));
		int row=0;
		panel.add(posterPreview,
			new GridBagConstraints(0, row, 1, 6, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(new JLabel("Serie:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
		panel.add(showField,
			new GridBagConstraints(2, row, 5, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(titleField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JScrollPane(genresTable),
			new GridBagConstraints(5, row, 1, 3, 0.5, 0.0, NORTHWEST, BOTH, new Insets(10, 10, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("German Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(germanTitleField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Index By:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(indexByField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Year:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(yearField,
			new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JLabel("Runtime:"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(runtimeField,
			new GridBagConstraints(4, row, 1, 1, 0.1, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Other Titles:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(new JScrollPane(tblNames),
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.5, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JScrollPane(languagesTable),
			new GridBagConstraints(5, row, 1, 1, 0.5, 0.0, NORTHWEST, BOTH, new Insets(10, 10, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("JS Call:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(javaScriptField,
			new GridBagConstraints(2, row, 1, 1, 0.2, 0.0, NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(recordField,
			new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JScrollPane(countriesTable),
			new GridBagConstraints(5, row, 1, 3, 0.5, 0.0, NORTHWEST, BOTH, new Insets(10, 10, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Script File:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(transcriptField,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		panel.add(new JLabel("Poster:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(posterField,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		titleField.getDocument().addDocumentListener(new FrameTitleUpdater());
		new PicturePreviewUpdater(posterField, posterPreview);
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
		return titleField;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=titleField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Movie: "+name);
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
