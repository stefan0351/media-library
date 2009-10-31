package com.kiwisoft.media;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.media.books.BooksTask;
import com.kiwisoft.media.dataimport.ImportChangesAction;
import com.kiwisoft.media.dataimport.TVTVDeLoaderAction;
import com.kiwisoft.media.fanfic.*;
import com.kiwisoft.media.links.LinksTask;
import com.kiwisoft.media.medium.AllMediaTask;
import com.kiwisoft.media.movie.MoviesTask;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonLookup;
import com.kiwisoft.media.person.PersonsTask;
import com.kiwisoft.media.photos.PhotosTask;
import com.kiwisoft.media.schedule.ScheduleTask;
import com.kiwisoft.media.show.GenreLookup;
import com.kiwisoft.media.show.ShowsTask;
import com.kiwisoft.swing.lookup.FileLookup;
import com.kiwisoft.swing.lookup.TableDialogLookupEditor;
import com.kiwisoft.swing.table.TableEditorFactory;

public class MediaManagerFrame extends ApplicationFrame
{
	public MediaManagerFrame()
	{
		super();
		String title="MediaManager v3.0";
		if (System.getProperty("media.database")!=null) title=title+" ("+System.getProperty("media.database")+")";
		setTitle(title);
	}

	@Override
	protected JMenuBar createMenu()
	{
		JMenu menuFile=new JMenu("Import/Export");
		menuFile.add(new ImportChangesAction(this));
		menuFile.addSeparator();
		menuFile.add(new ExportWebDatesAction());
		menuFile.addSeparator();
//		menuFile.add(new ProSiebenDeLoaderAction(this));
		menuFile.add(new TVTVDeLoaderAction(this));

		JMenuBar menuBar=new JMenuBar();
		menuBar.add(menuFile);
		return menuBar;
	}

	@Override
	protected JPanel getIntroPanel()
	{
		return new IntroPanel();
	}

	@Override
	protected void initializeTableComponents()
	{
		super.initializeTableComponents();
		TableEditorFactory editorFactory=TableEditorFactory.getInstance();
		editorFactory.setEditor(Language.class, new LanguageLookup());
		editorFactory.setEditor(Pairing.class, new PairingLookup());
		editorFactory.setEditor(FanDom.class, new FanDomLookup());
		editorFactory.setEditor(Author.class, new AuthorLookup());
		editorFactory.setEditor(Genre.class, new GenreLookup());
		editorFactory.setEditor(Country.class, new CountryLookup());
		editorFactory.setEditor(Person.class, new PersonLookup());
		editorFactory.setEditor(String.class, "FanFicPart", new TableDialogLookupEditor(new FanFicPartLookup()));
		editorFactory.setEditor(String.class, "File", new TableDialogLookupEditor(new FileLookup(JFileChooser.FILES_ONLY, false)));
		editorFactory.setEditor(String.class, "ExistingFile", new TableDialogLookupEditor(new FileLookup(JFileChooser.FILES_ONLY, true)));
		editorFactory.setEditor(String.class, "Directory", new TableDialogLookupEditor(new FileLookup(JFileChooser.DIRECTORIES_ONLY, false)));
		editorFactory.setEditor(String.class, "ExistingDirectory", new TableDialogLookupEditor(new FileLookup(JFileChooser.DIRECTORIES_ONLY, true)));
	}

	@Override
	protected List<MenuSidebarItem.Task> getTasks()
	{
		List<MenuSidebarItem.Task> tasks=new ArrayList<MenuSidebarItem.Task>(8);
		tasks.add(new BooksTask());
		if (MediaConfiguration.isFanFicsEnabled()) tasks.add(new FanFicTask());
		tasks.add(new LinksTask());
		tasks.add(new AllMediaTask());
		tasks.add(new MoviesTask());
		tasks.add(new PersonsTask());
		tasks.add(new PhotosTask());
		tasks.add(new ScheduleTask());
		tasks.add(new ShowsTask());
		tasks.add(new DataTask());
		return tasks;
	}
}
