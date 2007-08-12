package com.kiwisoft.media.show;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.medium.TrackDetailsAction;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;
import com.kiwisoft.utils.gui.table.TableController;

public class ShowTracksView extends ViewPanel
{
	private Show show;

	private TableController<Track> tableController;

	public ShowTracksView(Show show)
	{
		this.show=show;
	}

	public String getTitle()
	{
		return show.getTitle()+" - Tracks";
	}

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		ShowTracksTableModel tableModel=new ShowTracksTableModel(show);
		tableModel.sort();

		tableController=new TableController<Track>(tableModel, new DefaultTableConfiguration(ShowTracksTableModel.class))
		{
			@Override
			public List<ContextAction<? super Track>> getToolBarActions()
			{
				List<ContextAction<? super Track>> actions=new ArrayList<ContextAction<? super Track>>(1);
				actions.add(new TrackDetailsAction());
				return actions;
			}

			@Override
			public ContextAction<Track> getDoubleClickAction()
			{
				return new TrackDetailsAction();
			}
		};
		return tableController.createComponent();
	}

	protected void installComponentListeners()
	{
		tableController.installListeners();
		super.installComponentListeners();
	}

	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), ShowTracksView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new ShowTracksView(show), true);
	}

}