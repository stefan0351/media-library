package com.kiwisoft.media;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JFileChooser;

import com.kiwisoft.media.fanfic.Author;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.*;
import com.kiwisoft.media.show.ShowsTask;
import com.kiwisoft.media.show.GenreLookup;
import com.kiwisoft.media.medium.AllMediaTask;
import com.kiwisoft.media.movie.MoviesTask;
import com.kiwisoft.media.dataImport.*;
import com.kiwisoft.media.person.*;
import com.kiwisoft.media.schedule.ScheduleTask;
import com.kiwisoft.media.photos.PhotosTask;
import com.kiwisoft.media.photos.PhotoGallery;
import com.kiwisoft.media.photos.PhotoGalleryFormat;
import com.kiwisoft.media.pics.PictureFormat;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.books.BooksTask;
import com.kiwisoft.media.links.LinksTask;
import com.kiwisoft.swing.lookup.TableDialogLookupEditor;
import com.kiwisoft.swing.lookup.FileLookup;
import com.kiwisoft.swing.table.TableEditorFactory;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.format.FormatManager;

public class MediaManagerFrame extends ApplicationFrame
{
	public MediaManagerFrame()
	{
		super();
		setTitle("MediaManager v2.0");
	}

	protected JMenuBar createMenu()
	{
		JMenu menuFile=new JMenu("Import/Export");
		menuFile.add(new ImportScheduleAction(MediaManagerFrame.this));
		menuFile.add(new ExportWebDatesAction());
		menuFile.addSeparator();
		menuFile.add(new ProSiebenDeLoaderAction(this));
		menuFile.add(new TVTVDeLoaderAction(this));

		JMenuBar menuBar=new JMenuBar();
		menuBar.add(menuFile);
		return menuBar;
	}

	protected JPanel getIntroPanel()
	{
		return new IntroPanel();
	}

	@Override
	protected void initializeFormats()
	{
		super.initializeFormats();
		FormatManager formatManager=FormatManager.getInstance();
		formatManager.setFormat(Language.class, new LanguageFormat());
		formatManager.setFormat(Country.class, new CountryFormat());
		formatManager.setFormat(Gender.class, new GenderFormat());
		formatManager.setFormat(Picture.class, new PictureFormat());
		formatManager.setFormat(PhotoGallery.class, new PhotoGalleryFormat());
		formatManager.setFormat(LinkGroup.class, new LinkGroupFormat());
		formatManager.setFormat(Link.class, new LinkFormat());
		formatManager.setFormat(FanDom.class, "linkable", new FanDomLinkableFormat());
	}

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