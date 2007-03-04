package com.kiwisoft.media;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.kiwisoft.media.actions.*;
import com.kiwisoft.media.fanfic.Author;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.Pairing;
import com.kiwisoft.media.fanfic.*;
import com.kiwisoft.media.show.ShowsTask;
import com.kiwisoft.media.show.GenreLookup;
import com.kiwisoft.media.video.AllVideosTask;
import com.kiwisoft.media.movie.MoviesTask;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.lookup.TableDialogLookupEditor;
import com.kiwisoft.utils.gui.table.ComboBoxObjectEditor;
import com.kiwisoft.utils.gui.table.EditorFactory;
import com.kiwisoft.utils.gui.table.RendererFactory;

public class MediaManagerFrame extends ApplicationFrame
{
	public MediaManagerFrame()
	{
		super();
		setTitle("MediaManager v2.0");
	}

	protected JMenuBar createMenu()
	{
		JMenu menuFile=new JMenu("Datei");
		menuFile.add(new ImportAirdatesAction(MediaManagerFrame.this));
		menuFile.add(new ExportWebDatesAction());
		menuFile.addSeparator();
		menuFile.add(new ImportEpisodesAction(MediaManagerFrame.this));

		JMenu menuDownloadDates=new JMenu("Download Termine");
		menuDownloadDates.add(new DownloadP7Action(this));
		menuDownloadDates.add(new DownloadTVTVAction(this));

		JMenu menuTools=new JMenu("Tools");
		menuTools.add(menuDownloadDates);

		JMenuBar menuBar=new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuTools);
		return menuBar;
	}

	protected JPanel getIntroPanel()
	{
		return new IntroPanel();
	}

	protected void initializeTableComponents()
	{
		RendererFactory.getInstance().setRenderer(Language.class, new LanguageFormat());
		ComboBoxObjectEditor editor=new ComboBoxObjectEditor(LanguageManager.getInstance().getLanguages().toArray());
		editor.setRenderer(new LanguageComboBoxRenderer());
		EditorFactory editorFactory=EditorFactory.getInstance();
		editorFactory.setDefaultEditor(Language.class, editor);
		editorFactory.setDefaultEditor(Pairing.class, new PairingLookup());
		editorFactory.setDefaultEditor(FanDom.class, new FanDomLookup());
		editorFactory.setDefaultEditor(Author.class, new AuthorLookup());
		editorFactory.setDefaultEditor(Genre.class, new GenreLookup());
		editorFactory.setEditor(String.class, "FanFicPart", new TableDialogLookupEditor(new FanFicPartLookup()));
		editorFactory.setEditor(String.class, "WebFile", new TableDialogLookupEditor(new WebFileLookup(true)));
	}

	protected List<MenuSidebarItem.Task> getTasks()
	{
		List<MenuSidebarItem.Task> tasks=new ArrayList<MenuSidebarItem.Task>(8);
		tasks.add(new ShowsTask());
		tasks.add(new MoviesTask());
		tasks.add(new AllVideosTask());
		tasks.add(new ChannelsTask());
		tasks.add(new AirdatesTask());
		tasks.add(new ActorsTask());
		if (Configurator.getInstance().getBoolean("fanfics.enabled", false)) tasks.add(new FanFicTask());
		return tasks;

	}
}