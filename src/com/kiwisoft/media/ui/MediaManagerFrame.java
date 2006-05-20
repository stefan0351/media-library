package com.kiwisoft.media.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.ShowCharacter;
import com.kiwisoft.media.actions.ExportWebDatesAction;
import com.kiwisoft.media.actions.FormatEpisodesAction;
import com.kiwisoft.media.actions.FormatOldEpisodesAction;
import com.kiwisoft.media.actions.ImportAirdatesAction;
import com.kiwisoft.media.fanfic.Author;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.Pairing;
import com.kiwisoft.media.ui.fanfic.*;
import com.kiwisoft.media.ui.show.ShowsTask;
import com.kiwisoft.media.ui.video.AllVideosTask;
import com.kiwisoft.media.ui.movie.MoviesTask;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.lookup.TableDialogLookupEditor;
import com.kiwisoft.utils.gui.lookup.TableLookupEditor;
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
		menuFile.add(new FormatEpisodesAction(MediaManagerFrame.this));
		menuFile.add(new FormatOldEpisodesAction(MediaManagerFrame.this));

		JMenu menuDownloadDates=new JMenu("Download Termine");
		menuDownloadDates.add(new DownloadPNWAction(this));
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
		editorFactory.setEditor(Language.class, EditorFactory.DEFAULT, editor);
		editorFactory.setEditor(Pairing.class, EditorFactory.DEFAULT, new TableLookupEditor(new PairingLookup()));
		editorFactory.setEditor(FanDom.class, EditorFactory.DEFAULT, new TableLookupEditor(new FanDomLookup()));
		editorFactory.setEditor(Author.class, EditorFactory.DEFAULT, new TableLookupEditor(new AuthorLookup()));
		editorFactory.setEditor(ShowCharacter.class, EditorFactory.DEFAULT, new TableLookupEditor(new CharacterLookup()));
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