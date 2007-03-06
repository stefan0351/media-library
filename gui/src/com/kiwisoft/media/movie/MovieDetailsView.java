package com.kiwisoft.media.movie;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.*;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.ObjectTableModel;

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
		DetailsDialog dialog;
		if (owner instanceof JFrame) dialog=new DetailsDialog((JFrame)owner, view);
		else dialog=new DetailsDialog((JDialog)owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.movie;
		return null;
	}

	private Show show;
	private Movie movie;

	// Konfigurations Panel
	private JTextField tfShow;
	private JTextField tfName;
	private JTextField tfGermanName;
	private JCheckBox cbRecord;
	private JTextField tfJavaScript;
	private DialogLookupField tfScriptFile;
	private NamesTableModel tmNames;
	private DialogLookupField posterField;
	private JFormattedTextField yearField;
	private JFormattedTextField runtimeField;
	private ObjectTableModel<Genre> genresModel;
	private ObjectTableModel<Language> languagesModel;
	private ObjectTableModel<Country> countriesModel;

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
		tfName.setText(text);
	}

	private void initializeData()
	{
		if (movie!=null)
		{
			tfName.setText(movie.getTitle());
			tfGermanName.setText(movie.getGermanTitle());
			if (movie.getShow()!=null) tfShow.setText(movie.getShow().getName());
			cbRecord.setSelected(movie.isRecord());
			tfScriptFile.setText(movie.getWebScriptFile());
			tfJavaScript.setText(movie.getJavaScript());
			yearField.setValue(movie.getYear());
			runtimeField.setValue(movie.getRuntime());
			Iterator<Name> it=movie.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=it.next();
				tmNames.addName(name.getName(), name.getLanguage());
			}
			genresModel.setObjects(movie.getGenres());
			languagesModel.setObjects(movie.getLanguages());
			countriesModel.setObjects(movie.getCountries());
			tmNames.sort();
			String posterMini=movie.getPosterMini();
			if (!StringUtils.isEmpty(posterMini))
			{
				posterMini=new File(Configurator.getInstance().getString(PATH_ROOT), posterMini).getAbsolutePath();
				posterField.setText(posterMini);
			}
		}
		else if (show!=null)
		{
			tfShow.setText(show.getName());
		}
	}

	public boolean apply()
	{
		String name=tfName.getText();
		String germanName=tfGermanName.getText();
		boolean record=cbRecord.isSelected();
		String script=tfScriptFile.getText();
		if (StringUtils.isEmpty(script)) script=null;
		String javascript=tfJavaScript.getText();
		if (StringUtils.isEmpty(javascript)) javascript=null;
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(germanName))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		Map<String, Language> names=tmNames.getNames();
		Collection<Language> languages=languagesModel.getObjects();
		Collection<Country> countries=countriesModel.getObjects();
		Collection<Genre> genres=genresModel.getObjects();
		String posterMini=posterField.getText();
		if (!StringUtils.isEmpty(posterMini))
		{
			try
			{
				posterMini=FileUtils.getRelativePath(Configurator.getInstance().getString(PATH_ROOT), posterMini);
				posterMini=StringUtils.replaceStrings(posterMini, "\\", "/");
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				posterField.requestFocus();
				return false;
			}
		}
		else posterMini=null;

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (movie==null)
			{
				if (show!=null) movie=show.createMovie();
				else movie=MovieManager.getInstance().createMovie(null);
			}
			movie.setTitle(name);
			movie.setGermanTitle(germanName);
			movie.setRecord(record);
			movie.setJavaScript(javascript);
			movie.setPosterMini(posterMini);
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
		posterField=new DialogLookupField(new WebFileLookup(false));
		ImagePanel posterPreview=new ImagePanel(new Dimension(150, 200));
		posterPreview.setBorder(new EtchedBorder());
		tfShow=new JTextField();
		tfShow.setEditable(false);
		tfName=new JTextField();
		tfGermanName=new JTextField();
		cbRecord=new JCheckBox("Record");
		tfScriptFile=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		tfJavaScript=new JTextField(20);
		tmNames=new NamesTableModel();
		yearField=UIUtils.createNumberField(Integer.class, 5, 1900, 2100);
		runtimeField=UIUtils.createNumberField(Integer.class, 5, 0, 500);
		SortableTable tblNames=new SortableTable(tmNames);
		tblNames.setPreferredScrollableViewportSize(new Dimension(300, 100));
		tblNames.initializeColumns(new MediaTableConfiguration("table.movie.names"));
		genresModel=new ObjectTableModel<Genre>("genres", Genre.class, null);
		SortableTable genresTable=new SortableTable(genresModel);
		genresTable.initializeColumns(new MediaTableConfiguration("table.movie"));
		languagesModel=new ObjectTableModel<Language>("languages", Language.class, null);
		SortableTable languagesTable=new SortableTable(languagesModel);
		languagesTable.initializeColumns(new MediaTableConfiguration("table.movie"));
		countriesModel=new ObjectTableModel<Country>("countries", Country.class, null);
		SortableTable countriesTable=new SortableTable(countriesModel);
		countriesTable.initializeColumns(new MediaTableConfiguration("table.movie"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(800, 340));
		int row=0;
		add(posterPreview,
			new GridBagConstraints(0, row, 1, 6, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("Serie:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(tfShow,
			new GridBagConstraints(2, row, 5, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfName,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(genresTable),
			new GridBagConstraints(5, row, 1, 3, 0.5, 0.0, NORTHWEST, BOTH, new Insets(10, 10, 0, 0), 0, 0));

		row++;
		add(new JLabel("German Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfGermanName,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Year:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(yearField,
			new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Runtime:"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(runtimeField,
			new GridBagConstraints(4, row, 1, 1, 0.1, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Other Titles:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(new JScrollPane(tblNames),
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.5, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(languagesTable),
			new GridBagConstraints(5, row, 1, 1, 0.5, 0.0, NORTHWEST, BOTH, new Insets(10, 10, 0, 0), 0, 0));

		row++;
		add(new JLabel("JS Call:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfJavaScript,
			new GridBagConstraints(2, row, 1, 1, 0.2, 0.0, NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(cbRecord,
			new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(countriesTable),
			new GridBagConstraints(5, row, 1, 3, 0.5, 0.0, NORTHWEST, BOTH, new Insets(10, 10, 0, 0), 0, 0));

		row++;
		add(new JLabel("Script File:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfScriptFile,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Poster (mini):"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(posterField,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));


		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
		new ImageUpdater(posterField.getTextField(), posterPreview);
	}

	public JComponent getDefaultFocusComponent()
	{
		return tfName;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Movie: "+name);
		}
	}
}
