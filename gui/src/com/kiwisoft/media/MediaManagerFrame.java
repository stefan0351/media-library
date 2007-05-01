package com.kiwisoft.media;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.kiwisoft.media.fanfic.Author;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.*;
import com.kiwisoft.media.show.ShowsTask;
import com.kiwisoft.media.show.GenreLookup;
import com.kiwisoft.media.video.AllVideosTask;
import com.kiwisoft.media.movie.MoviesTask;
import com.kiwisoft.media.dataImport.*;
import com.kiwisoft.media.person.PersonsTask;
import com.kiwisoft.media.person.Gender;
import com.kiwisoft.media.person.GenderFormat;
import com.kiwisoft.media.schedule.ScheduleTask;
import com.kiwisoft.media.photos.PhotosTask;
import com.kiwisoft.media.pics.PictureFormat;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.format.FormatManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.lookup.TableDialogLookupEditor;
import com.kiwisoft.utils.gui.table.TableEditorFactory;

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
		menuFile.add(new ImportAirdatesAction(MediaManagerFrame.this));
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
		editorFactory.setEditor(String.class, "FanFicPart", new TableDialogLookupEditor(new FanFicPartLookup()));
		editorFactory.setEditor(String.class, "WebFile", new TableDialogLookupEditor(new WebFileLookup(true)));
	}

	protected List<MenuSidebarItem.Task> getTasks()
	{
		List<MenuSidebarItem.Task> tasks=new ArrayList<MenuSidebarItem.Task>(8);
		tasks.add(new ShowsTask());
		tasks.add(new MoviesTask());
		tasks.add(new AllVideosTask());
		tasks.add(new PhotosTask());
		tasks.add(new ChannelsTask());
		tasks.add(new ScheduleTask());
		tasks.add(new PersonsTask());
		if (Configurator.getInstance().getBoolean("fanfics.enabled", false)) tasks.add(new FanFicTask());
		tasks.add(new DataTask());
		return tasks;

	}
}