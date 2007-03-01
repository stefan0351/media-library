package com.kiwisoft.media.ui.movie;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.io.IOException;
import java.io.File;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.*;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieInfo;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.media.movie.MovieType;
import com.kiwisoft.media.ui.show.WebInfosTableModel;
import com.kiwisoft.media.ui.NamesTableModel;
import com.kiwisoft.media.ui.MediaManagerFrame;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsDialog;

public class MovieDetailsView extends DetailsView
{
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
	private JTextField tfOriginalName;
	private JCheckBox cbSeen;
	private JCheckBox cbRecord;
	private JCheckBox cbGood;
	private JTextField tfJavaScript;
	private LookupField<MovieType> tfType;
	private DialogLookupField tfScriptFile;
	private DynamicTable tblNames;
	private NamesTableModel tmNames;
	private DynamicTable tblInfos;
	private WebInfosTableModel tmInfos;
	private JComboBox cbxInfoTypes;

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
			tfName.setText(movie.getName());
			tfOriginalName.setText(movie.getOriginalName());
			if (movie.getShow()!=null) tfShow.setText(movie.getShow().getName());
			cbSeen.setSelected(movie.isSeen());
			cbRecord.setSelected(movie.isRecord());
			cbGood.setSelected(movie.isGood());
			tfScriptFile.setText(movie.getWebScriptFile());
			tfJavaScript.setText(movie.getJavaScript());
			tfType.setValue(movie.getType());
			Iterator<Name> it=movie.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=it.next();
				tmNames.addName(name.getName(), name.getLanguage());
			}
			tmNames.sort();
			for (Iterator it2=movie.getInfos().iterator(); it2.hasNext();)
			{
				MovieInfo info=(MovieInfo)it2.next();
				tmInfos.addInfo(info);
			}
			tmInfos.sort();
		}
		else if (show!=null)
		{
			tfShow.setText(show.getName());
		}
	}

	public boolean apply()
	{
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
		List<WebInfosTableModel.Row> infos=new ArrayList<WebInfosTableModel.Row>();
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
		Map<String, Language> names=tmNames.getNames();
		MovieType type=tfType.getValue();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (movie==null)
			{
				if (show!=null) movie=show.createMovie();
				else movie=MovieManager.getInstance().createMovie(null);
			}
			movie.setName(name);
			movie.setOriginalName(originalName);
			movie.setType(type);
			movie.setRecord(record);
			movie.setSeen(seen);
			movie.setGood(good);
			movie.setJavaScript(javascript);
			movie.setWebScriptFile(script);
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
			for (Iterator<WebInfosTableModel.Row> it2=infos.iterator(); it2.hasNext();)
			{
				WebInfosTableModel.Row row=it2.next();
				MovieInfo info=(MovieInfo)row.getUserObject();
				if (info==null)
				{
					info=movie.createInfo();
					row.setUserObject(info);
				}
				info.setName(row.getName());
				info.setPath(row.getPath());
				info.setLanguage(row.getLanguage());
				if (row.isDefault()) movie.setDefaultInfo(info);
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
		cbGood=new JCheckBox("Sehr Gut");
		cbSeen=new JCheckBox("Gesehen");
		cbRecord=new JCheckBox("Aufnehmen");
		tfScriptFile=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		tfJavaScript=new JTextField();
		tmNames=new NamesTableModel();
		tblNames=new DynamicTable(tmNames);
		tblNames.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.movie.names"));
		tmInfos=new WebInfosTableModel(true);
		tblInfos=new DynamicTable(tmInfos);
		tblInfos.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.movie.infos"));
		Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
		Language english=LanguageManager.getInstance().getLanguageBySymbol("en");
		cbxInfoTypes=new JComboBox(new Object[]{
			"<Leer>",
			new InfoType("Beschreibung", "index.xp", german),
			new InfoType("Bilder", "gallery.xp", english),
			new InfoType("Transkript", "transcript.xp", english)
		});
		cbxInfoTypes.setEditable(false);
		tfType=new LookupField<MovieType>(new MovieTypeLookup());

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(800, 500));
		int row=0;
		add(new JLabel("Serie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Originalname:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfOriginalName, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Typ:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfType, new GridBagConstraints(1, row, 1, 1, 0.3, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Skriptdatei:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfScriptFile, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("JavaScript:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfJavaScript, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Alternative Titel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblNames), new GridBagConstraints(1, row, 3, 3, 1.0, 0.5,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(cbSeen, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(cbRecord, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(cbGood, new GridBagConstraints(4, row, 1, 1, 0.0, 0.2,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Seiten:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblInfos), new GridBagConstraints(1, row, 4, 1, 1.0, 0.5,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(cbxInfoTypes, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		add(new JButton(new NewInfoAction()), new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
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
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("Film: "+name);
		}
	}

	private class InfoType
	{
		private String name;
		private String fileName;
		private Language language;

		public InfoType(String name, String fileName, Language language)
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
			StringBuffer path=new StringBuffer("movies");
			path.append(File.separator).append("name");
			path.append(File.separator).append(fileName);
			File file=new File(Configurator.getInstance().getString("path.root"), path.toString());
			row.setPath(file.getAbsolutePath());
			row.setLanguage(language);
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
			if (type instanceof InfoType)
			{
				InfoType infoType=(InfoType)type;
				infoType.initRow(row);
			}
		}
	}

}
